package nl.jaysh.recipe.feature.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import nl.jaysh.recipe.R
import nl.jaysh.recipe.core.designsystem.theme.RecipeTheme
import nl.jaysh.recipe.core.designsystem.theme.blue
import nl.jaysh.recipe.core.designsystem.theme.orange
import nl.jaysh.recipe.core.domain.model.detail.Instruction
import nl.jaysh.recipe.core.domain.model.detail.InstructionStep
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import nl.jaysh.recipe.core.ui.composables.RecipeAsyncImage
import nl.jaysh.recipe.core.ui.composables.RecipeErrorLayout
import nl.jaysh.recipe.core.ui.composables.RecipeLoadingLayout
import nl.jaysh.recipe.core.utils.Constants.CDN_BASE_URL

@Preview
@Composable
private fun RecipeDetailScreenPreview() = RecipeTheme {
    RecipeDetailScreenContent(
        state = RecipeDetailViewModelState(),
        onClickBack = {},
        onClickFavourite = { _, _ -> },
    )
}

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecipeDetailScreenContent(
        state = state,
        onClickBack = onClickBack,
        onClickFavourite = viewModel::setFavourite
    )
}

@Composable
private fun RecipeDetailScreenContent(
    state: RecipeDetailViewModelState,
    onClickBack: () -> Unit,
    onClickFavourite: (RecipeDetail, Boolean) -> Unit,
) {
    when (state.fetchedRecipeDetail) {
        FetchRecipeDetailState.Loading -> RecipeLoadingLayout(
            modifier = Modifier.fillMaxSize(),
        )

        is FetchRecipeDetailState.Error -> RecipeErrorLayout(
            modifier = Modifier.fillMaxSize(),
            failure = state.fetchedRecipeDetail.failure,
        )

        is FetchRecipeDetailState.Success -> RecipeDetailContent(
            recipeDetail = state.fetchedRecipeDetail.detail,
            onClickBack = onClickBack,
            onClickFavourite = onClickFavourite,
        )
    }
}

@Composable
private fun RecipeDetailContent(
    recipeDetail: RecipeDetail,
    onClickBack: () -> Unit,
    onClickFavourite: (RecipeDetail, Boolean) -> Unit,
) {
    Column {
        RecipeDetailTopBar(
            recipeDetail = recipeDetail,
            onClickBack = onClickBack,
            onClickFavourite = onClickFavourite,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Header(
                    modifier = Modifier.fillMaxWidth(),
                    title = recipeDetail.title,
                    url = recipeDetail.image,
                    readyInMinutes = recipeDetail.readyInMinutes.toString(),
                )
            }

            item {
                RecipeDetailSection(title = stringResource(R.string.recipe_detail_ingredients)) {
                    recipeDetail.extendedIngredients.forEach { ingredient ->
                        Text(
                            text = ingredient.original,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            item {
                RecipeDetailSection(title = stringResource(R.string.recipe_detail_instructions)) {
                    recipeDetail.analyzedInstructions.forEach { instruction ->
                        InstructionSteps(instruction)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeDetailTopBar(
    recipeDetail: RecipeDetail,
    onClickBack: () -> Unit,
    onClickFavourite: (RecipeDetail, Boolean) -> Unit,
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
) {
    IconButton(
        onClick = onClickBack,
        content = {
            Icon(
                painter = painterResource(R.drawable.back),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = "back button",
            )
        }
    )

    val isFavourite = recipeDetail.favourite ?: false
    IconButton(
        onClick = { onClickFavourite(recipeDetail, !isFavourite) },
        content = {
            Icon(
                painter = if (isFavourite) painterResource(R.drawable.heart)
                else painterResource(R.drawable.outline_heart),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = "favourite button",
            )
        }
    )
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String,
    url: String,
    readyInMinutes: String,
) = Box(modifier = modifier) {
    HeaderImage(
        modifier = Modifier.fillMaxSize(),
        url = url,
    )

    Text(
        modifier = Modifier
            .padding(all = 8.dp)
            .align(Alignment.TopStart),
        text = title,
        style = MaterialTheme.typography.displaySmall,
        color = Color.White,
    )

    Text(
        modifier = Modifier
            .padding(all = 8.dp)
            .align(Alignment.BottomEnd),
        text = "$readyInMinutes min.",
        style = MaterialTheme.typography.headlineMedium,
        color = Color.White,
    )
}

@Composable
fun HeaderImage(modifier: Modifier = Modifier, url: String) = AsyncImage(
    modifier = modifier,
    model = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(enable = true)
        .build(),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    colorFilter = ColorFilter.tint(
        color = blue.copy(alpha = 0.5f),
        blendMode = BlendMode.SrcAtop,
    ),
)

@Composable
private fun RecipeDetailSection(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit,
) = Card(
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = orange,
        )

        HorizontalDivider()

        content()
    }
}

@Composable
private fun InstructionSteps(instruction: Instruction) {
    instruction.steps.forEach { step ->
        Card {
            InstructionListItem(step)
        }
    }
}

@Composable
private fun InstructionListItem(step: InstructionStep) = ListItem(
    leadingContent = {
        InstructionImage(
            modifier = Modifier.size(64.dp),
            url = step.ingredients.firstOrNull()?.image,
            stepNumber = step.number.toString(),
        )
    },
    headlineContent = {
        Text(
            text = "Step: ${step.number}",
            style = MaterialTheme.typography.labelSmall,
        )
    },
    supportingContent = {
        Text(
            text = step.step,
            style = MaterialTheme.typography.bodySmall,
        )
    }
)

@Composable
private fun InstructionImage(
    modifier: Modifier = Modifier,
    url: String? = null,
    stepNumber: String,
) {
    if (url == null) {
        InstructionImagePlaceholder(
            modifier = modifier,
            stepNumber = stepNumber,
        )
    } else {
        RecipeAsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            url = "$CDN_BASE_URL/$url",
        )
    }
}

@Composable
private fun InstructionImagePlaceholder(
    modifier: Modifier = Modifier,
    stepNumber: String,
) = Box(
    modifier = modifier
        .clip(shape = CircleShape)
        .background(color = orange),
    contentAlignment = Alignment.Center,
) {
    Text(
        text = stepNumber,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displayMedium,
        color = Color.White,
    )
}
