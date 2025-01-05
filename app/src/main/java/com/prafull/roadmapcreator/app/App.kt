package com.prafull.roadmapcreator.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.prafull.roadmapcreator.app.graph.GraphScreen
import com.prafull.roadmapcreator.app.history.RoadmapHistoryScreen
import com.prafull.roadmapcreator.app.home.HomeScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel


sealed interface Screens {
    @Serializable
    data object Home : Screens


    @Serializable
    data class GraphScreen(val graph: String, val title: String = "") : Screens

    @Serializable
    data object HistoryScreen : Screens
}

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.Home) {
        composable<Screens.Home> {
            MainScreen(BottomNavItems.HOME.route, navController)
        }
        composable<Screens.GraphScreen> {
            GraphScreen(
                graph = it.toRoute<Screens.GraphScreen>().graph,
                title = it.toRoute<Screens.GraphScreen>().title,
                navController = navController
            )
        }
        composable<Screens.HistoryScreen> {
            MainScreen(BottomNavItems.HISTORY.route, navController)
        }
    }
}

enum class BottomNavItems(val route: String) {
    HOME("home"), HISTORY("history")
}

@Composable
private fun MainScreen(
    currentScreen: String,
    navController: NavController,
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                Modifier.fillMaxWidth()
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    BottomNavItems.entries.forEach { item ->
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    imageVector = when (item) {
                                        BottomNavItems.HOME -> Icons.Default.Home
                                        BottomNavItems.HISTORY -> Icons.AutoMirrored.Filled.List
                                    },
                                    contentDescription = item.name
                                )
                            },
                            label = { Text(item.name) },
                            selected = currentScreen == item.route,
                            onClick = {
                                if (currentScreen != item.route) {
                                    if (item == BottomNavItems.HOME) {
                                        navController.navigate(Screens.Home)
                                    } else {
                                        navController.navigate(Screens.HistoryScreen)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it), contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                BottomNavItems.HOME.route -> HomeScreen(getViewModel(), navController)
                BottomNavItems.HISTORY.route -> RoadmapHistoryScreen(getViewModel(), navController)
            }
        }
    }
}