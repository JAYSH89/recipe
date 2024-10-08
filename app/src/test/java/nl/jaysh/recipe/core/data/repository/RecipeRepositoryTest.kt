package nl.jaysh.recipe.core.data.repository

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import nl.jaysh.recipe.core.data.local.room.dao.RecipeDetailDao
import nl.jaysh.recipe.core.data.network.service.RecipeRemoteDataSource
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.failure.StorageFailure
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects.testRecipeDetailDTO
import nl.jaysh.recipe.helper.objects.SearchRecipeObjects
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepositoryTest {

    private lateinit var dao: RecipeDetailDao
    private lateinit var service: RecipeRemoteDataSource
    private lateinit var repository: RecipeRepository

    private val testQuery = "Lasagna"
    private val testRecipeId = 640864L

    @BeforeEach
    fun setup() {
        dao = mockk()
        service = mockk()
        repository = RecipeRepositoryImpl(
            dao = dao,
            recipeRemoteDataSource = service,
            dispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `search successful`() = runTest {
        val response = Either.Right(SearchRecipeObjects.searchResponseDTO)
        coEvery { service.search(query = any()) } returns response

        val result = repository.search(query = testQuery).first()
        assertThat(result.getOrNull()?.size).isEqualTo(2)

        coVerify(exactly = 1) { service.search(query = testQuery) }
    }

    @Test
    fun `getDetails NOT cached should fetch from network and save record in DB`() = runTest {
        coEvery { dao.getById(id = any()) } returns flowOf(null)
        coEvery { dao.save(recipe = any()) } returns Unit
        val response = Either.Right(testRecipeDetailDTO)
        coEvery { service.getDetails(recipeId = any()) } returns response

        repository.getDetails(recipeId = testRecipeId).first()

        coVerify(exactly = 1) { dao.getById(id = testRecipeId) }
        coVerify(exactly = 1) { service.getDetails(recipeId = any()) }
        coVerify(exactly = 1) { dao.save(recipe = any()) }
    }

    @Test
    fun `getDetails in cache do not fetch recipeDetail from network`() = runTest {
        coEvery { dao.getById(id = any()) } returns flowOf(RecipeDetailObjects.recipeDetailEntity)
        coEvery { dao.save(recipe = any()) } returns Unit
        val response = Either.Right(testRecipeDetailDTO)
        coEvery { service.getDetails(recipeId = any()) } returns response

        repository.getDetails(recipeId = testRecipeId).first()

        coVerify(exactly = 1) { dao.getById(id = testRecipeId) }
        coVerify(exactly = 0) { service.getDetails(recipeId = any()) }
        coVerify(exactly = 0) { dao.save(recipe = any()) }
    }

    @Test
    fun `getDetails NOT cached can throw error saving to db`() = runTest {
        coEvery { dao.getById(id = any()) } returns flowOf(null)
        val successResponse = Either.Right(testRecipeDetailDTO)
        coEvery { service.getDetails(recipeId = any()) } returns successResponse
        coEvery { dao.save(recipe = any()) } throws RuntimeException()

        val result = repository.getDetails(recipeId = testRecipeId).first()
        assertThat(result?.leftOrNull()).isEqualTo(StorageFailure.IO)

        coVerify(exactly = 1) { dao.save(any()) }
    }

    @Test
    fun `set as favourite should update in db`() = runTest {
        coEvery { dao.updateFavouriteStatus(recipeId = any(), isFavourite = true) } returns Unit

        repository.setFavouriteRecipe(recipeId = testRecipeId, isFavourite = true)

        coVerify(exactly = 1) { dao.updateFavouriteStatus(testRecipeId, true) }
    }

    @Test
    fun `get favourite recipe should get from db successful`() = runTest {
        val entities = listOf(
            RecipeDetailObjects.recipeDetailEntity.copy(id = 1L),
            RecipeDetailObjects.recipeDetailEntity.copy(id = 2L),
        )
        coEvery { dao.getFavourites() } returns flowOf(entities)

        val result = repository.getFavouriteRecipe().first().getOrNull()
        assertThat(result?.size).isEqualTo(2)
        assertThat(result?.get(0)?.id).isEqualTo(1L)
        assertThat(result?.get(1)?.id).isEqualTo(2L)

        coVerify(exactly = 1) { dao.getFavourites() }
    }
}
