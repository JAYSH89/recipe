package nl.jaysh.recipe.feature.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.jaysh.recipe.R
import nl.jaysh.recipe.core.domain.model.search.SearchResult
import nl.jaysh.recipe.core.designsystem.theme.RecipeTheme
import nl.jaysh.recipe.core.designsystem.theme.blue
import nl.jaysh.recipe.core.ui.composables.RecipeAsyncImage
import nl.jaysh.recipe.core.ui.composables.RecipeErrorLayout
import nl.jaysh.recipe.core.ui.composables.RecipeLoadingLayout

@Preview
@Composable
private fun RecipeOverviewScreenPreview() = RecipeTheme {
    RecipeOverviewContent(
        state = RecipeOverviewViewModelState(),
        onSearch = {},
        onSelectRecipe = {},
    )
}

@Composable
fun RecipeOverviewScreen(
    viewModel: RecipeOverviewViewModel = hiltViewModel(),
    onSelectRecipe: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecipeOverviewContent(
        state = state,
        onSearch = viewModel::onSearch,
        onSelectRecipe = onSelectRecipe,
    )
}

@Composable
private fun RecipeOverviewContent(
    state: RecipeOverviewViewModelState,
    onSearch: (String) -> Unit,
    onSelectRecipe: (Long) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        val colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = input,
            colors = colors,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search",
                )
            },
            placeholder = { Text(text = "Search") },
            onValueChange = { value ->
                input = value
                onSearch(value)
            },
        )

        when (state.fetchedRecipes) {
            FetchRecipeState.Loading -> RecipeLoadingLayout(
                modifier = Modifier.fillMaxSize(),
            )

            is FetchRecipeState.Error -> RecipeErrorLayout(
                modifier = Modifier.fillMaxSize(),
                failure = state.fetchedRecipes.failure,
            )

            is FetchRecipeState.Success -> RecipeOverview(
                searchResults = state.fetchedRecipes.recipes,
                onSelectRecipe = onSelectRecipe,
            )
        }
    }
}

@Composable
private fun RecipeOverview(
    modifier: Modifier = Modifier,
    searchResults: List<SearchResult>,
    onSelectRecipe: (Long) -> Unit,
) = LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
) {
    items(searchResults) { recipe ->
        RecipeCard(
            recipe = recipe,
            onClick = onSelectRecipe,
        )
    }
}

@Composable
private fun RecipeCard(recipe: SearchResult, onClick: (Long) -> Unit) = Card {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick(recipe.id) },
    ) {
        RecipeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            url = recipe.image,
            colorFilter = ColorFilter.tint(
                color = blue.copy(alpha = 0.5f),
                blendMode = BlendMode.SrcAtop,
            ),
        )

        Text(
            modifier = Modifier
                .padding(all = 8.dp)
                .align(Alignment.TopStart),
            text = recipe.title,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
        )

        Text(
            modifier = Modifier
                .padding(all = 8.dp)
                .align(Alignment.BottomEnd),
            text = "${recipe.readyInMinutes} min.",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
        )
    }
}
