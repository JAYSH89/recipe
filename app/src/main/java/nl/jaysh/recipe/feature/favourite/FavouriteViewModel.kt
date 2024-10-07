package nl.jaysh.recipe.feature.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: RecipeRepository,
) : ViewModel() {

    val state: StateFlow<FavouriteViewModelState> = getFavouriteRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = FavouriteViewModelState.Loading,
        )

    private fun getFavouriteRecipes() = flow {
        emit(FavouriteViewModelState.Loading)

        repository
            .getFavouriteRecipe()
            .collect { recipes ->
                recipes.fold(
                    ifLeft = { failure ->
                        emit(FavouriteViewModelState.Error(failure))
                    },
                    ifRight = { favouriteRecipes ->
                        emit(FavouriteViewModelState.Success(favouriteRecipes))
                    },
                )
            }
    }
}

sealed interface FavouriteViewModelState {
    data object Loading : FavouriteViewModelState
    data class Error(val failure: Failure) : FavouriteViewModelState
    data class Success(val favouriteRecipes: List<RecipeDetail>) : FavouriteViewModelState
}
