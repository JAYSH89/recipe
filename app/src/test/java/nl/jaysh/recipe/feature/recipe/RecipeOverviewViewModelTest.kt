package nl.jaysh.recipe.feature.recipe

import app.cash.turbine.test
import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.failure.NetworkFailure
import nl.jaysh.recipe.helper.FakeRecipeRepository
import nl.jaysh.recipe.helper.objects.SearchRecipeObjects
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeOverviewViewModelTest {

    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: RecipeOverviewViewModel

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = FakeRecipeRepository()
        viewModel = RecipeOverviewViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cancel()
    }

    @Test
    fun `successful fetch recipes initially`() = runTest {
        viewModel.state.test {
            val initialEmission = awaitItem()
            val initialState = RecipeOverviewViewModelState()
            assertThat(initialEmission).isEqualTo(initialState)

            val successEmission = awaitItem()
            val success = SearchRecipeState.Success(SearchRecipeObjects.testSearchResults)
            val successState = initialState.copy(searchResults = success)
            assertThat(successEmission).isEqualTo(successState)

            expectNoEvents()
        }
    }

    @Test
    fun `unsuccessful fetch recipes initially`() = runTest {
        val fakeNetworkDelay = 200L
        val failingRepository = mockk<RecipeRepository>()
        every { failingRepository.search(query = any()) } returns flow {
            delay(fakeNetworkDelay)
            emit(Either.Left(NetworkFailure.UNAUTHORIZED))
        }

        val failingViewModel = RecipeOverviewViewModel(failingRepository)
        failingViewModel.state.test {
            val initialEmission = awaitItem()
            val initialState = RecipeOverviewViewModelState()
            assertThat(initialEmission).isEqualTo(initialState)

            val failureEmission = awaitItem()
            val expectedFailure = SearchRecipeState.Error(NetworkFailure.UNAUTHORIZED)
            val failureState = initialState.copy(searchResults = expectedFailure)
            assertThat(failureEmission).isEqualTo(failureState)

            expectNoEvents()
        }

        verify(exactly = 1) { failingRepository.search(query = any()) }
    }

    @Test
    fun `search query should fetch recipes successful`() = runTest {
        viewModel.state.test {
            skipItems(2)

            viewModel.onSearch("Ginger")
            advanceTimeBy(FakeRecipeRepository.FAKE_DELAY)

            val searchEmission = awaitItem()
            val searchLoadingState = RecipeOverviewViewModelState(
                query = "Ginger",
                searchResults = SearchRecipeState.Loading,
            )
            assertThat(searchEmission).isEqualTo(searchLoadingState)

            val finalEmission = awaitItem()
            val finalState = RecipeOverviewViewModelState(
                query = "Ginger",
                searchResults = SearchRecipeState.Success(
                    recipes = SearchRecipeObjects.testSearchResults
                ),
            )
            assertThat(finalEmission).isEqualTo(finalState)

            expectNoEvents()
        }
    }
}
