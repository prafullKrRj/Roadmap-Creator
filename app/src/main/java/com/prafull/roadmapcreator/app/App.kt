package com.prafull.roadmapcreator.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel


sealed interface Screens {
    @Serializable
    data object Home : Screens


    @Serializable
    data class GraphScreen(val graph: String, val title: String = "") : Screens
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.Home) {
        composable<Screens.Home> {
            HomeScreen(viewModel = getViewModel(), navController)
        }
        composable<Screens.GraphScreen> {
            GraphScreen(
                graph = it.toRoute<Screens.GraphScreen>().graph,
                title = it.toRoute<Screens.GraphScreen>().title,
                navController = navController
            )
        }
    }
}