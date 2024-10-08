package nl.jaysh.recipe.core.data.network.service

import arrow.core.Either
import nl.jaysh.recipe.core.data.network.model.detail.RecipeDetailDTO
import nl.jaysh.recipe.core.data.network.model.search.SearchResponseDTO
import nl.jaysh.recipe.core.domain.model.failure.Failure

interface RecipeRemoteDataSource {
    suspend fun search(query: String): Either<Failure, SearchResponseDTO>
    suspend fun getDetails(recipeId: Long): Either<Failure, RecipeDetailDTO>
}
