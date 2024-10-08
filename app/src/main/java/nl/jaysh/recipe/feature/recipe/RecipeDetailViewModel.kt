package nl.jaysh.recipe.feature.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.jaysh.recipe.core.domain.RecipeRepository
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.failure.UnknownFailure
import nl.jaysh.recipe.core.ui.navigation.NavigationConstants.RECIPE_DETAIL_KEY
import nl.jaysh.recipe.feature.recipe.FetchRecipeDetailState.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val recipeId = savedStateHandle
        .getStateFlow<Long?>(key = RECIPE_DETAIL_KEY, initialValue = null)

    val state: StateFlow<RecipeDetailViewModelState> = recipeId
        .flatMapLatest(::fetchRecipeDetail)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = RecipeDetailViewModelState(),
        )

    fun setFavourite(recipeId: Long, isFavourite: Boolean) {
        viewModelScope.launch {
            repository.setFavouriteRecipe(recipeId = recipeId, isFavourite = isFavourite)
        }
    }

    private fun fetchRecipeDetail(recipeId: Long?): Flow<RecipeDetailViewModelState> = flow {
        if (recipeId == null) {
            val failure = UnknownFailure.Unspecified
            emit(RecipeDetailViewModelState(fetchedRecipeDetail = Error(failure)))
            return@flow
        }

        val newState = RecipeDetailViewModelState(recipeId, Loading)
        emit(newState)

        repository
            .getDetails(recipeId)
            .collect { recipeDetail ->
                recipeDetail?.let {
                    it.fold(
                        ifLeft = { failure ->
                            emit(newState.copy(fetchedRecipeDetail = Error(failure)))
                        },
                        ifRight = { recipe ->
                            emit(newState.copy(fetchedRecipeDetail = Success(recipe)))
                        },
                    )
                }
            }
    }
}

data class RecipeDetailViewModelState(
    val recipeId: Long? = null,
    val fetchedRecipeDetail: FetchRecipeDetailState = Loading,
)

sealed interface FetchRecipeDetailState {
    data object Loading : FetchRecipeDetailState
    data class Error(val failure: Failure) : FetchRecipeDetailState
    data class Success(val detail: RecipeDetail) : FetchRecipeDetailState
}
