package nl.jaysh.recipe.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nl.jaysh.recipe.feature.home.HomeScreen

@Composable
fun RootNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.HOME) {
        composable(Destination.HOME) {
            HomeScreen(rootNavController = navController)
        }
    }
}
