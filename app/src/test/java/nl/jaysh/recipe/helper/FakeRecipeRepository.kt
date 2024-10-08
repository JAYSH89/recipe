package nl.jaysh.recipe.helper

import arrow.core.Either
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.search.SearchResult
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects
import nl.jaysh.recipe.helper.objects.SearchRecipeObjects

class FakeRecipeRepository : RecipeRepository {

    private var recipes: List<SearchResult> = SearchRecipeObjects.testSearchResults
    private val recipeDetail = MutableStateFlow(RecipeDetailObjects.testRecipeDetail)

    override fun search(query: String): Flow<Either<Failure, List<SearchResult>>> {
        return flow {
            delay(FAKE_DELAY)
            emit(Either.Right(recipes.toList()))
        }
    }

    override fun getDetails(recipeId: Long): Flow<Either<Failure, RecipeDetail>?> {
        return recipeDetail.map { Either.Right(it) }
            .onEach { delay(FAKE_DELAY) }
    }

    override suspend fun setFavouriteRecipe(recipe: RecipeDetail, isFavourite: Boolean) {
        val updatedRecipe = RecipeDetailObjects.testRecipeDetail.copy(favourite = isFavourite)
        recipeDetail.update { updatedRecipe }
    }

    override fun getFavouriteRecipe(): Flow<Either<Failure, List<RecipeDetail>>> {
        return recipeDetail
            .map { favourite -> Either.Right(listOf(favourite)) }
            .onEach { delay(FAKE_DELAY) }
    }

    companion object {
        const val FAKE_DELAY: Long = 100
    }
}