package nl.jaysh.recipe.core.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.jaysh.recipe.core.designsystem.theme.RecipeTheme
import nl.jaysh.recipe.core.designsystem.theme.orange

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun RecipeLoadingLayoutPreviewLight() = RecipeTheme {
    RecipeLoadingLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun RecipeLoadingLayoutPreviewDark() = RecipeTheme {
    RecipeLoadingLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    )
}

@Composable
fun RecipeLoadingLayout(modifier: Modifier = Modifier) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    content = { CircularProgressIndicator(color = orange) }
)
