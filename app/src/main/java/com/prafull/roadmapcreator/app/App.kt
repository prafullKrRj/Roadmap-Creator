package com.prafull.roadmapcreator.app

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel


sealed interface Screens {
    @Serializable
    data object Home : Screens

    @Serializable
    data class History(val historyId: Long) : Screens
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.Home) {
        composable<Screens.Home> {
            HomeScreen(viewModel = getViewModel())
        }
        composable<Screens.History> {
            Text(text = "History")
        }
    }
}