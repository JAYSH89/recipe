package nl.jaysh.recipe.core.data.network.service

import arrow.core.Either
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException
import nl.jaysh.recipe.core.data.network.model.detail.RecipeDetailDTO
import nl.jaysh.recipe.core.data.network.model.search.SearchResponseDTO
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.failure.NetworkFailure
import nl.jaysh.recipe.core.domain.model.failure.ParseFailure
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
) : RecipeRemoteDataSource {

    override suspend fun search(query: String): Either<Failure, SearchResponseDTO> {
        val path = "/recipes/complexSearch"

        return Either.catch {
            val response = httpClient.get(path) {
                parameter("query", query)
                parameter("addRecipeInformation", true)
            }

            response.body<SearchResponseDTO>()
        }.mapLeft(::mapToFailure)
    }

    override suspend fun getDetails(recipeId: Long): Either<Failure, RecipeDetailDTO> {
        val path = "/recipes/$recipeId/information"

        return Either.catch {
            val response = httpClient.get(path)
            response.body<RecipeDetailDTO>()
        }.mapLeft(::mapToFailure)
    }

    private fun mapToFailure(throwable: Throwable): Failure {
        return when (throwable) {
            is TimeoutCancellationException,
            is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException,
            -> NetworkFailure.TIMEOUT

            is IOException -> NetworkFailure.NO_INTERNET

            is SerializationException,
            is NoTransformationFoundException,
            -> ParseFailure.JsonParse

            is ClientRequestException -> {
                when (throwable.response.status.value) {
                    401 -> NetworkFailure.UNAUTHORIZED
                    402 -> NetworkFailure.PAYMENT_REQUIRED
                    404 -> NetworkFailure.NOT_FOUND
                    else -> NetworkFailure.UNKNOWN
                }
            }

            else -> NetworkFailure.UNKNOWN
        }
    }
}
