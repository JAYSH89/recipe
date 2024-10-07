package nl.jaysh.recipe.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import nl.jaysh.recipe.core.ui.navigation.NavigationConstants.RECIPE_DETAIL_KEY
import nl.jaysh.recipe.feature.favourite.FavouriteScreen
import nl.jaysh.recipe.feature.recipe.RecipeDetailScreen
import nl.jaysh.recipe.feature.recipe.RecipeOverviewScreen
import nl.jaysh.recipe.feature.settings.SettingsScreen

@Composable
fun HomeNavHost(rootNavController: NavController, homeNavController: NavHostController) {
    NavHost(navController = homeNavController, startDestination = Destination.OVERVIEW) {
        composable(Destination.OVERVIEW) {
            RecipeOverviewScreen { recipeId ->
                val route = Destination.recipeDetail(recipeId)
                homeNavController.navigate(route)
            }
        }

        composable(Destination.FAVOURITE) {
            FavouriteScreen { recipeId ->
                val route = Destination.recipeDetail(recipeId)
                homeNavController.navigate(route)
            }
        }

        composable(Destination.SETTINGS) {
            SettingsScreen()
        }

        composable(
            route = "${Destination.DETAIL}?$RECIPE_DETAIL_KEY={$RECIPE_DETAIL_KEY}",
            arguments = listOf(navArgument(name = RECIPE_DETAIL_KEY) { type = NavType.LongType }),
            content = { RecipeDetailScreen(onClickBack = { homeNavController.navigateUp() }) }
        )
    }
}
