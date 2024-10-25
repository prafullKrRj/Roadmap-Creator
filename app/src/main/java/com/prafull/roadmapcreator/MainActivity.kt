package com.prafull.roadmapcreator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.prafull.roadmapcreator.app.MainScreen
import com.prafull.roadmapcreator.ui.theme.RoadmapCreatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoadmapCreatorTheme {
                MainScreen()
            }
        }
    }
}