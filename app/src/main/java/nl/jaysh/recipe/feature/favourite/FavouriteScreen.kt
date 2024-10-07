package nl.jaysh.recipe.feature.favourite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.jaysh.recipe.R
import nl.jaysh.recipe.core.designsystem.theme.RecipeTheme
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.ui.composables.RecipeAsyncImage
import nl.jaysh.recipe.core.ui.composables.RecipeErrorLayout
import nl.jaysh.recipe.core.ui.composables.RecipeLoadingLayout

@Preview
@Composable
private fun FavouriteScreenPreview() = RecipeTheme {
    FavouriteScreenContent(state = FavouriteViewModelState.Loading, onClick = {})
}

@Composable
fun FavouriteScreen(
    viewModel: FavouriteViewModel = hiltViewModel(),
    onClick: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FavouriteScreenContent(state = state, onClick = onClick)
}

@Composable
private fun FavouriteScreenContent(
    state: FavouriteViewModelState,
    onClick: (Long) -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
) {
    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = stringResource(R.string.favourites),
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )

    when (state) {
        FavouriteViewModelState.Loading -> RecipeLoadingLayout(
            modifier = Modifier.fillMaxSize(),
        )

        is FavouriteViewModelState.Error -> RecipeErrorLayout(
            modifier = Modifier.fillMaxSize(),
            failure = state.failure,
        )

        is FavouriteViewModelState.Success -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.favouriteRecipes) { recipe ->
                FavouriteListItem(recipe = recipe, onClick = onClick)
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun FavouriteListItem(recipe: RecipeDetail, onClick: (Long) -> Unit) = Card {
    ListItem(
        modifier = Modifier.clickable { onClick(recipe.id) },
        leadingContent = {
            RecipeAsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                url = recipe.image,
            )
        },
        headlineContent = {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        supportingContent = {
            Text(
                text = "Duration ${recipe.readyInMinutes} min.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    )
}
