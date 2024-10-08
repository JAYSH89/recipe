package nl.jaysh.recipe.helper

import arrow.core.Either
import nl.jaysh.recipe.core.data.network.model.detail.RecipeDetailDTO
import nl.jaysh.recipe.core.data.network.model.search.SearchResponseDTO
import nl.jaysh.recipe.core.data.network.service.RecipeRemoteDataSource
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects
import nl.jaysh.recipe.helper.objects.SearchRecipeObjects

class FakeRecipeRemoteDataSource : RecipeRemoteDataSource {

    private var searchResponse = SearchRecipeObjects.searchResponseDTO
    private var informationResponse = RecipeDetailObjects.testRecipeDetailDTO

    override suspend fun search(query: String): Either<Failure, SearchResponseDTO> {
        return Either.Right(searchResponse)
    }

    override suspend fun getDetails(recipeId: Long): Either<Failure, RecipeDetailDTO> {
        return Either.Right(informationResponse)
    }
}