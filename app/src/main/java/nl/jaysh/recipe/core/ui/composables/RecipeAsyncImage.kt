package nl.jaysh.recipe.core.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RecipeAsyncImage(
    modifier: Modifier = Modifier,
    url: String,
    colorFilter: ColorFilter? = null,
) = AsyncImage(
    modifier = modifier,
    model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(enable = true)
        .build(),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    colorFilter = colorFilter,
)
