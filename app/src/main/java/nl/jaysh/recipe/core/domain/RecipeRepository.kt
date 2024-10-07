package nl.jaysh.recipe.core.domain

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.search.SearchRecipeResult

interface RecipeRepository {
    fun searchRecipes(query: String): Flow<Either<Failure, List<SearchRecipeResult>>>
    fun fetchRecipeDetail(recipeId: Long): Flow<Either<Failure, RecipeDetail>?>
    suspend fun setFavouriteRecipe(recipeId: Long, isFavourite: Boolean)
    fun getFavouriteRecipe(): Flow<Either<Failure, List<RecipeDetail>>>
}
