package nl.jaysh.recipe.feature.recipe

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.failure.NetworkFailure
import nl.jaysh.recipe.core.domain.model.failure.UnknownFailure
import nl.jaysh.recipe.core.ui.navigation.NavigationConstants.RECIPE_DETAIL_KEY
import nl.jaysh.recipe.helper.FakeRecipeRepository
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailViewModelTest {

    private lateinit var repository: RecipeRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: RecipeDetailViewModel

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val recipeId: Long = 1

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
        savedStateHandle = SavedStateHandle(mapOf(RECIPE_DETAIL_KEY to recipeId))
        viewModel = RecipeDetailViewModel(
            repository = repository,
            savedStateHandle = savedStateHandle,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cancel()
    }

    @Test
    fun `savedStateHandle null should throw`() = runTest {
        val emptySavedStateHandle = SavedStateHandle()

        val viewModel = RecipeDetailViewModel(
            repository = FakeRecipeRepository(),
            savedStateHandle = emptySavedStateHandle,
        )

        viewModel.state.test {
            val initialEmission = awaitItem()
            val expectedFailure = FetchRecipeDetailState.Error(UnknownFailure.Unspecified)
            assertThat(initialEmission.fetchedRecipeDetail).isEqualTo(expectedFailure)
        }
    }

    @Test
    fun `savedStateHandle with value should set recipeId`() = runTest {
        every { repository.getDetails(recipeId = any()) } returns emptyFlow()

        viewModel.state.test {
            val initialEmission = awaitItem()
            assertThat(initialEmission.recipeId).isEqualTo(recipeId)
            assertThat(initialEmission.fetchedRecipeDetail).isEqualTo(FetchRecipeDetailState.Loading)

            expectNoEvents()
        }
    }

    @Test
    fun `should fetch recipe detail initially`() = runTest {
        val fakeNetworkDelay = 200L
        every { repository.getDetails(recipeId = any()) } returns flow {
            emit(null)
            delay(fakeNetworkDelay)
            emit(Either.Right(RecipeDetailObjects.recipeDetail))
        }

        viewModel.state.test {
            skipItems(1)
            advanceTimeBy(fakeNetworkDelay)

            val finalEmission = awaitItem()
            assertThat(finalEmission.recipeId).isEqualTo(recipeId)
            assertThat(finalEmission.fetchedRecipeDetail).isInstanceOf(FetchRecipeDetailState.Success::class)

            expectNoEvents()
        }

        verify(exactly = 1) { repository.getDetails(recipeId = recipeId) }
    }

    @Test
    fun `fetch recipe detail failure should error`() = runTest {
        val fakeNetworkDelay = 200L
        every { repository.getDetails(recipeId = any()) } returns flow {
            emit(null)
            delay(fakeNetworkDelay)
            emit(Either.Left(NetworkFailure.UNAUTHORIZED))
        }

        viewModel.state.test {
            skipItems(1)
            advanceTimeBy(fakeNetworkDelay)

            val failureEmission = awaitItem()
            val expectedFailure = FetchRecipeDetailState.Error(NetworkFailure.UNAUTHORIZED)
            assertThat(failureEmission.recipeId).isEqualTo(recipeId)
            assertThat(failureEmission.fetchedRecipeDetail).isEqualTo(expectedFailure)

            expectNoEvents()
        }

        verify(exactly = 1) { repository.getDetails(recipeId = recipeId) }
    }
}