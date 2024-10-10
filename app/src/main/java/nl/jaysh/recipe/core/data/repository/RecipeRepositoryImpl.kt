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
import nl.jaysh.recipe.core.data.network.model.detail.RecipeDetailDTO
import nl.jaysh.recipe.core.data.network.model.detail.toIngredient
import nl.jaysh.recipe.core.data.network.model.detail.toInstruction
import nl.jaysh.recipe.core.data.network.model.detail.toRecipeDetail
import nl.jaysh.recipe.core.data.network.model.search.toSearchResult
import nl.jaysh.recipe.core.data.network.service.RecipeRemoteDataSource
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.failure.ParseFailure
import nl.jaysh.recipe.core.domain.model.failure.StorageFailure
import nl.jaysh.recipe.core.domain.model.search.SearchResult
import nl.jaysh.recipe.core.utils.sequence
import nl.jaysh.recipe.di.dispatcher.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val dao: RecipeDetailDao,
    private val recipeRemoteDataSource: RecipeRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : RecipeRepository {

    override fun search(query: String): Flow<Either<Failure, List<SearchResult>>> {
        return flow {
            val searchResults = recipeRemoteDataSource.search(query)
                .map { response -> response.results }
                .map { results -> results.map { it.toSearchResult() } }

            emit(searchResults)
        }.flowOn(context = dispatcher)
    }

    override fun getDetails(): Flow<Either<Failure, List<RecipeDetail>>> {
        return dao.getRecipes().map(::convertToRecipeDetailList)
    }

    override fun getDetailsById(recipeId: Long): Flow<Either<Failure, RecipeDetail>> {
        return dao.getById(id = recipeId).map { recipeEntity ->
            recipeEntity
                ?.let { convertToRecipeDetail(it) }
                ?: retrieveAndSaveRecipeDetail(recipeId)
        }.flowOn(context = dispatcher)
    }

    override suspend fun setFavouriteRecipe(recipe: RecipeDetail, isFavourite: Boolean) {
        dao.updateFavouriteStatus(
            recipeId = recipe.id,
            isFavourite = isFavourite,
        )
    }

    override fun getFavouriteRecipe(): Flow<Either<Failure, List<RecipeDetail>>> = dao
        .getFavourites()
        .map(::convertToRecipeDetailList)
        .flowOn(context = dispatcher)

    private suspend fun retrieveAndSaveRecipeDetail(recipeId: Long): Either<Failure, RecipeDetail> {
        val recipeDetailDTO = recipeRemoteDataSource.getDetails(recipeId)

        return recipeDetailDTO.flatMap { detailDTO ->
            val recipeEntity = convertToRecipeDetailEntity(detailDTO)
            val savedEntity = saveRecipeDetailEntity(recipeEntity)
            return savedEntity.map { detailDTO.toRecipeDetail() }
        }
    }

    private fun saveRecipeDetailEntity(
        entity: RecipeDetailEntity,
    ): Either<Failure, RecipeDetailEntity> = try {
        dao.save(entity)
        Either.Right(entity)
    } catch (e: RuntimeException) {
        Either.Left(StorageFailure.IO)
    }

    /**
     * For 'simplicity' we encode + store the list of ingredients and list of instructions as
     * JSON string. On retrieval we decode the JSON string back to objects.
     */
    private fun convertToRecipeDetailEntity(recipeDetailDTO: RecipeDetailDTO): RecipeDetailEntity {
        val recipeDetail = recipeDetailDTO.toRecipeDetail()
        val analyzedInstructions = Json.encodeToString(recipeDetailDTO.analyzedInstructions)
        val extendedIngredients = Json.encodeToString(recipeDetailDTO.extendedIngredients)

        return RecipeDetailEntity.fromRecipeDetail(
            recipeDetail = recipeDetail,
            analyzedInstructions = analyzedInstructions,
            extendedIngredients = extendedIngredients,
        )
    }

    private fun convertToRecipeDetailList(
        details: List<RecipeDetailEntity>,
    ): Either<Failure, List<RecipeDetail>> {
        return details.map(::convertToRecipeDetail).sequence()
    }

    private fun convertToRecipeDetail(entity: RecipeDetailEntity): Either<Failure, RecipeDetail> {
        val analyzedInstructions = entity.analyzedInstructions
        val extendedIngredients = entity.extendedIngredients

        try {
            val instructions = Json.decodeFromString<List<InstructionDTO>>(analyzedInstructions)
                .map { it.toInstruction() }

            val ingredients = Json.decodeFromString<List<IngredientDTO>>(extendedIngredients)
                .map { it.toIngredient() }

            val recipeDetail = entity.toRecipeDetail(instructions, ingredients)

            return Either.Right(recipeDetail)
        } catch (e: SerializationException) {
            return Either.Left(ParseFailure.JsonParse)
        } catch (e: IllegalArgumentException) {
            return Either.Left(ParseFailure.JsonParse)
        }
    }
}
