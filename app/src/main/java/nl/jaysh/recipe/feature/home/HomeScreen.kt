package nl.jaysh.recipe.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import nl.jaysh.recipe.core.ui.navigation.HomeNavHost

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    rootNavController: NavController,
    homeNavController: NavHostController = rememberNavController(),
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background)
        .statusBarsPadding()
        .imePadding(),
) {
    Scaffold(
        bottomBar = {
            if (!WindowInsets.isImeVisible)
                HomeBottomNavigation(navController = homeNavController)
        },
        containerColor = Color.Transparent,
        content = { padding -> App(padding, rootNavController, homeNavController) }
    )
}

@Composable
private fun App(
    contentPadding: PaddingValues,
    rootNavController: NavController,
    homeNavController: NavHostController,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding),
    content = { HomeNavHost(rootNavController, homeNavController) }
)
