package nl.jaysh.recipe.helper

import arrow.core.Either
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.search.SearchResult
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects

class FakeRecipeRepository : RecipeRepository {

    private var recipes: List<SearchResult> = mutableListOf()
    private var recipeDetail = RecipeDetailObjects.testRecipeDetail

    override fun search(query: String): Flow<Either<Failure, List<SearchResult>>> {
        return flow {
            // Fake fetching data delay
            delay(FAKE_DELAY)

            emit(Either.Right(recipes.toList()))
        }
    }

    override fun getDetails(recipeId: Long): Flow<Either<Failure, RecipeDetail>?> = flow {
        // Fake fetching data delay
        delay(FAKE_DELAY)

        emit(Either.Right(recipeDetail))
    }

    override suspend fun setFavouriteRecipe(recipeId: Long, isFavourite: Boolean) {}

    override fun getFavouriteRecipe(): Flow<Either<Failure, List<RecipeDetail>>> = emptyFlow()

    companion object {
        const val FAKE_DELAY: Long = 100
    }
}