package com.prafull.roadmapcreator.app

import androidx.compose.material3.Text
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
    data class History(val historyId: Long) : Screens

    @Serializable
    data class GraphScreen(val graph: String) : Screens
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.Home) {
        composable<Screens.Home> {
            HomeScreen(viewModel = getViewModel(), navController)
        }
        composable<Screens.History> {
            Text(text = "History")
        }
        composable<Screens.GraphScreen> {
            GraphScreen(
                graph = it.toRoute<Screens.GraphScreen>().graph,
                navController = navController
            )
        }
    }
}