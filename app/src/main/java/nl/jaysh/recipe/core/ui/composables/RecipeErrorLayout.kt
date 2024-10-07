package nl.jaysh.recipe.core.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.jaysh.recipe.R
import nl.jaysh.recipe.core.designsystem.theme.RecipeTheme
import nl.jaysh.recipe.core.domain.model.failure.Failure
import nl.jaysh.recipe.core.domain.model.failure.NetworkFailure

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun RecipeErrorLayoutPreview() = RecipeTheme {
    RecipeErrorLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        failure = NetworkFailure.NOT_FOUND,
    )
}

@Composable
fun RecipeErrorLayout(modifier: Modifier = Modifier, failure: Failure) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
) {
    Text(
        text = "⚠\uFE0F",
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(12.dp))

    FailureMessage(failure = failure)
}

@Composable
private fun FailureMessage(failure: Failure) {
    val text = when (failure) {
        NetworkFailure.TIMEOUT -> stringResource(R.string.error_message_timeout)
        NetworkFailure.NO_INTERNET -> stringResource(R.string.error_message_no_internet)
        NetworkFailure.UNAUTHORIZED -> stringResource(R.string.error_message_unauthorized)
        NetworkFailure.NOT_FOUND -> stringResource(R.string.error_message_not_found)
        NetworkFailure.PAYMENT_REQUIRED -> stringResource(R.string.error_message_payment_required)
        else -> stringResource(R.string.something_went_wrong)
    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}
