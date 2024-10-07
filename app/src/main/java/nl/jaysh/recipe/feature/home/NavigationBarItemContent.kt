package nl.jaysh.recipe.feature.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import nl.jaysh.recipe.R
import nl.jaysh.recipe.core.ui.navigation.Destination

sealed class NavigationBarItemContent(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
) {
    data object Overview : NavigationBarItemContent(
        route = Destination.OVERVIEW,
        title = R.string.bottom_bar_overview,
        selectedIcon = R.drawable.article,
        unselectedIcon = R.drawable.outline_article,
    )

    data object Favourite : NavigationBarItemContent(
        route = Destination.FAVOURITE,
        title = R.string.bottom_bar_favourites,
        selectedIcon = R.drawable.bookmarks,
        unselectedIcon = R.drawable.outline_bookmarks,
    )

    data object Settings : NavigationBarItemContent(
        route = Destination.SETTINGS,
        title = R.string.bottom_bar_settings,
        selectedIcon = R.drawable.settings,
        unselectedIcon = R.drawable.outline_settings,
    )
}
