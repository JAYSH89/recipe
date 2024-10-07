package nl.jaysh.recipe.core.data.repository

import arrow.core.Either
import arrow.core.flatMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.jaysh.recipe.core.data.local.room.dao.RecipeDetailDao
import nl.jaysh.recipe.core.data.local.room.entity.RecipeDetailEntity
import nl.jaysh.recipe.core.data.local.room.entity.toRecipeDetail
import nl.jaysh.recipe.core.data.network.model.detail.IngredientDTO
import nl.jaysh.recipe.core.data.network.model.detail.InstructionDTO
import nl.jaysh.recipe.core.data.network.model.detail.toIngredient
import nl.jaysh.recipe.core.data.network.model.detail.toInstruction
import nl.jaysh.recipe.core.data.network.model.detail.toRecipeDetail
import nl.jaysh.recipe.core.data.network.model.search.toSearchResult
import nl.jaysh.recipe.core.data.network.service.RecipeService
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.failure.ParseFailure
import nl.jaysh.recipe.core.domain.model.failure.StorageFailure
import nl.jaysh.recipe.core.domain.model.search.SearchRecipeResult
import nl.jaysh.recipe.core.utils.sequence
import nl.jaysh.recipe.di.dispatcher.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val dao: RecipeDetailDao,
    private val recipeService: RecipeService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : RecipeRepository {

    override fun searchRecipes(query: String): Flow<Either<Failure, List<SearchRecipeResult>>> {
        return flow {
            val searchResults = recipeService.searchRecipes(query)
                .map { response -> response.results }
                .map { results -> results.map { it.toSearchResult() } }

            emit(searchResults)
        }.flowOn(context = dispatcher)
    }

    override fun fetchRecipeDetail(recipeId: Long): Flow<Either<Failure, RecipeDetail>> {
        return dao
            .getById(id = recipeId)
            .map { recipeEntity ->
                recipeEntity
                    ?.decodeRecipeDetail()
                    ?: fetchAndStoreRecipeDetail(recipeId)
            }
            .flowOn(context = dispatcher)
    }

    override suspend fun setFavouriteRecipe(recipeId: Long, isFavourite: Boolean) {
        dao.updateFavouriteStatus(recipeId = recipeId, isFavourite = isFavourite)
    }

    override fun getFavouriteRecipe(): Flow<Either<Failure, List<RecipeDetail>>> = dao
        .getFavourites()
        .map { entities ->
            entities.map { entity -> entity.decodeRecipeDetail() }.sequence()
        }
        .flowOn(context = dispatcher)

    // For 'simplicity' maps List<Instruction> and List<Ingredient> as Json String to store
    private suspend fun fetchAndStoreRecipeDetail(recipeId: Long): Either<Failure, RecipeDetail> {
        return recipeService.fetchRecipeDetail(recipeId)
            .flatMap { recipeDetailDTO ->
                val recipeDetail = recipeDetailDTO.toRecipeDetail()
                val recipeEntity = RecipeDetailEntity.fromRecipeDetail(
                    recipeDetail = recipeDetail,
                    analyzedInstructions = Json.encodeToString(recipeDetailDTO.analyzedInstructions),
                    extendedIngredients = Json.encodeToString(recipeDetailDTO.extendedIngredients),
                )

                try {
                    dao.save(recipeEntity)
                    Either.Right(recipeDetail)
                } catch (e: RuntimeException) {
                    Either.Left(StorageFailure.IO)
                }
            }
    }

    // For 'simplicity' decodes back to List<Instruction> and List<Ingredient>
    private fun RecipeDetailEntity.decodeRecipeDetail(): Either<Failure, RecipeDetail> {
        try {
            val instructions = Json.decodeFromString<List<InstructionDTO>>(analyzedInstructions)
            val ingredients = Json.decodeFromString<List<IngredientDTO>>(extendedIngredients)

            val recipeDetail = toRecipeDetail(
                analyzedInstructions = instructions.map { it.toInstruction() },
                extendedIngredients = ingredients.map { it.toIngredient() },
            )

            return Either.Right(recipeDetail)
        } catch (e: SerializationException) {
            return Either.Left(ParseFailure.JsonParse)
        } catch (e: IllegalArgumentException) {
            return Either.Left(ParseFailure.JsonParse)
        }
    }
}
