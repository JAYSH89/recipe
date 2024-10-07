package nl.jaysh.recipe.core.ui.navigation

import nl.jaysh.recipe.core.ui.navigation.NavigationConstants.RECIPE_DETAIL_KEY

object Destination {
    const val HOME = "home"
    const val OVERVIEW = "recipe_overview"
    const val DETAIL = "recipe_detail"
    const val FAVOURITE = "favorites"
    const val SETTINGS = "settings"

    fun recipeDetail(recipeId: Long): String = "$DETAIL?$RECIPE_DETAIL_KEY=$recipeId"
}
