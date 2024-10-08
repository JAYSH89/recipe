package nl.jaysh.recipe.feature.favourite

import app.cash.turbine.test
import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
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
import nl.jaysh.recipe.helper.FakeRecipeRepository
import nl.jaysh.recipe.helper.objects.RecipeDetailObjects
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteViewModelTest {

    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: FavouriteViewModel

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = FakeRecipeRepository()
        viewModel = FavouriteViewModel(repository = repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cancel()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.state.test {
            val initialEmission = awaitItem()
            assertThat(initialEmission).isEqualTo(FavouriteViewModelState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetch recipes successful state is Success`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(FavouriteViewModelState.Loading)
            advanceTimeBy(FakeRecipeRepository.FAKE_DELAY)

            val expected = listOf(RecipeDetailObjects.testRecipeDetail)
            assertThat(awaitItem()).isEqualTo(FavouriteViewModelState.Success(expected))

            expectNoEvents()
        }
    }
}