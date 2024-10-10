package nl.jaysh.recipe.feature.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.search.SearchResult
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecipeOverviewViewModel @Inject constructor(
    private val repository: RecipeRepository,
) : ViewModel() {

    private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    val state: StateFlow<RecipeOverviewViewModelState> = searchQuery
        .asStateFlow()
        .debounce(500L)
        .flatMapLatest(::searchRecipes)
        .combine(repository.getDetails(), transform = ::mapDetails)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = RecipeOverviewViewModelState(),
        )

    fun onSearch(query: String) {
        searchQuery.update { query }
    }

    private fun searchRecipes(query: String): Flow<RecipeOverviewViewModelState> = flow {
        val newState = RecipeOverviewViewModelState(query, SearchRecipeState.Loading)
        emit(newState)

        repository
            .search(query)
            .collect { result ->
                result.fold(
                    ifLeft = { failure ->
                        val error = SearchRecipeState.Error(failure)
                        emit(newState.copy(searchResults = error))
                    },
                    ifRight = { recipes ->
                        val searchResults = SearchRecipeState.Success(recipes)
                        emit(newState.copy(searchResults = searchResults))
                    },
                )
            }
    }

    private fun mapDetails(
        currentState: RecipeOverviewViewModelState,
        historyResult: Either<Failure, List<RecipeDetail>>,
    ): RecipeOverviewViewModelState = historyResult.fold(
        ifLeft = { failure ->
            val error = HistoryState.Error(failure = failure)
            currentState.copy(recipeHistory = error)
        },
        ifRight = { history ->
            val recipeHistory = HistoryState.Success(history = history)
            currentState.copy(recipeHistory = recipeHistory)
        },
    )

//    private fun mapDetailsTheSecond(
//        currentState: RecipeOverviewViewModelState,
//        historyResult: Either<Failure, List<RecipeDetail>>,
//    ): RecipeOverviewViewModelState {
//        historyResult.fold(
//            ifLeft = { failure ->
//                val error = FetchState.Error(failure = failure)
//                currentState.copy(fetcher = error)
//            },
//            ifRight = { history ->
//                val recipeHistory = FetchState.Success<List<RecipeDetail>>(values = history)
//                currentState.copy(fetcher = recipeHistory)
//            },
//    }
}

data class RecipeOverviewViewModelState(
    val query: String = "",
    val searchResults: SearchRecipeState = SearchRecipeState.Loading,
    val recipeHistory: HistoryState = HistoryState.NoHistory,
//    val fetcher: FetchState = FetchState.Initial,
)

sealed interface SearchRecipeState {
    data object Loading : SearchRecipeState
    data class Error(val failure: Failure) : SearchRecipeState
    data class Success(val recipes: List<SearchResult>) : SearchRecipeState
}

sealed interface HistoryState {
    data object NoHistory : HistoryState
    data class Error(val failure: Failure) : HistoryState
    data class Success(val history: List<RecipeDetail>) : HistoryState
}

//sealed interface FetchState {
//    data object Initial : FetchState
//    data class Error(val failure: Failure) : FetchState
//    data class Success<T>(val values: T) : FetchState
//}
