package nl.jaysh.recipe.helper

import arrow.core.Either
import nl.jaysh.recipe.core.data.network.model.detail.RecipeDetailDTO
import nl.jaysh.recipe.core.data.network.model.search.SearchResponseDTO
import nl.jaysh.recipe.core.data.network.service.RecipeService
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects
import nl.jaysh.recipe.helper.objects.SearchRecipeObjects

class FakeRecipeService : RecipeService {

    private var searchResponse = SearchRecipeObjects.searchResponseDTO
    private var informationResponse = RecipeDetailObjects.testRecipeDetailDTO

    override suspend fun searchRecipes(query: String): Either<Failure, SearchResponseDTO> {
        return Either.Right(searchResponse)
    }

    override suspend fun fetchRecipeDetail(recipeId: Long): Either<Failure, RecipeDetailDTO> {
        return Either.Right(informationResponse)
    }
}