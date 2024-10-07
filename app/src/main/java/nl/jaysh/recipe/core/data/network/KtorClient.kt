package nl.jaysh.recipe.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import nl.jaysh.recipe.BuildConfig
import nl.jaysh.recipe.core.utils.Constants

object KtorClient {
    fun createHttpClient(engine: HttpClientEngine) = HttpClient(engine) {
        expectSuccess = true

        configureContentNegotiation()
        configureDefaultRequest()
    }

    private fun HttpClientConfig<*>.configureContentNegotiation() {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    private fun HttpClientConfig<*>.configureHttpTimeOut() {
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
            connectTimeoutMillis = 5_000
        }
    }

    private fun HttpClientConfig<*>.configureDefaultRequest() {
        defaultRequest {
            contentType(ContentType.Application.Json)

            url {
                protocol = URLProtocol.HTTPS
                host = Constants.API_HOST
                parameters.append("apiKey", BuildConfig.API_KEY)
            }
        }
    }
}
