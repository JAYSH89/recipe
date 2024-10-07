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
        repository = mockk()
        viewModel = FavouriteViewModel(repository = repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cancel()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        every { repository.getFavouriteRecipe() } returns emptyFlow()

        viewModel.state.test {
            val initialEmission = awaitItem()
            assertThat(initialEmission).isEqualTo(FavouriteViewModelState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetch recipes successful state is Success`() = runTest {
        val simulateDelay = 100L
        every { repository.getFavouriteRecipe() } returns flow {
            delay(simulateDelay) // simulate delay
            emit(Either.Right(emptyList()))
        }

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(FavouriteViewModelState.Loading)
            advanceTimeBy(simulateDelay)
            assertThat(awaitItem()).isEqualTo(FavouriteViewModelState.Success(emptyList()))
            expectNoEvents()
        }
    }
}