package nl.jaysh.recipe.feature.recipe

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.search.SearchRecipeResult
import nl.jaysh.recipe.feature.recipe.FetchRecipeState.Error
import nl.jaysh.recipe.feature.recipe.FetchRecipeState.Loading
import nl.jaysh.recipe.feature.recipe.FetchRecipeState.Success
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
        .flatMapLatest(::fetchRecipes)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = RecipeOverviewViewModelState(),
        )

    fun onSearch(query: String) {
        searchQuery.update { query }
    }

    private fun fetchRecipes(query: String): Flow<RecipeOverviewViewModelState> = flow {
        val newState = RecipeOverviewViewModelState(query, Loading)
        emit(newState)

        repository
            .searchRecipes(query)
            .collect { result ->
                result.fold(
                    ifLeft = { failure ->
                        val error = Error(failure)
                        emit(newState.copy(fetchedRecipes = error))
                    },
                    ifRight = { recipes ->
                        val fetchedRecipes = Success(recipes)
                        emit(newState.copy(fetchedRecipes = fetchedRecipes))
                    },
                )
            }
    }
}

data class RecipeOverviewViewModelState(
    val query: String = "",
    val fetchedRecipes: FetchRecipeState = Loading,
)

sealed interface FetchRecipeState {
    data object Loading : FetchRecipeState
    data class Error(val failure: Failure) : FetchRecipeState

    @Immutable
    data class Success(val recipes: List<SearchRecipeResult>) : FetchRecipeState
}
