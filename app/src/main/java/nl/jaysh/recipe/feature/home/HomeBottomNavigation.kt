package nl.jaysh.recipe.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun HomeBottomNavigation(navController: NavHostController) {
    val navigationBarItems = remember {
        listOf(
            NavigationBarItemContent.Overview,
            NavigationBarItemContent.Favourite,
            NavigationBarItemContent.Settings,
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val tabBarDestination = navigationBarItems.any { it.route == currentDestination?.route }

    if (tabBarDestination) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 8.dp,
        ) {
            navigationBarItems.forEach { navigationBarItemContent ->
                BottomNavigationItem(
                    navigationBarItemContent = navigationBarItemContent,
                    currentDestination = currentDestination,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItem(
    navigationBarItemContent: NavigationBarItemContent,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    val selected = currentDestination?.hierarchy
        ?.any { it.route == navigationBarItemContent.route } == true

    val navigationBarItemColors = NavigationBarItemColors(
        selectedIconColor = LocalContentColor.current,
        selectedTextColor = LocalContentColor.current,
        selectedIndicatorColor = Color.Transparent,
        unselectedIconColor = LocalContentColor.current.copy(alpha = 0.3f),
        unselectedTextColor = LocalContentColor.current.copy(alpha = 0.3f),
        disabledIconColor = Color.Gray,
        disabledTextColor = Color.Gray,
    )

    NavigationBarItem(
        selected = selected,
        colors = navigationBarItemColors,
        label = {
            BottomNavigationLabel(
                text = stringResource(navigationBarItemContent.title),
                selected = selected,
            )
        },
        icon = {
            Icon(
                painter = if (selected) painterResource(navigationBarItemContent.selectedIcon)
                else painterResource(navigationBarItemContent.unselectedIcon),
                contentDescription = stringResource(navigationBarItemContent.title),
            )
        },
        onClick = {
            if (!selected) navController.navigate(navigationBarItemContent.route) {
                popUpTo(navController.graph.findStartDestination().navigatorName)
                launchSingleTop = true
            }
        },
    )
}

@Composable
private fun BottomNavigationLabel(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
) {
    Text(text = text, style = MaterialTheme.typography.labelSmall)
    Box(
        modifier = Modifier
            .size(4.dp)
            .background(
                color = MaterialTheme.colorScheme.error.copy(
                    alpha = if (selected) 1.0f else 0.0f
                ),
                shape = CircleShape
            ),
    )
}
