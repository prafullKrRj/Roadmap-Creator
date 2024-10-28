package com.prafull.roadmapcreator.app

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prafull.roadmapcreator.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: AppViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    val history by viewModel.history.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(drawerContent = {
        ModalDrawerSheet {
            LazyColumn {
                item {
                    NavigationDrawerItem(label = {
                        Text(text = "History", fontSize = 22.sp)
                    }, selected = false, onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        viewModel.clearState()
                    }, shape = RectangleShape, icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_history_24),
                            contentDescription = "History"
                        )
                    })
                }
                item {
                    HorizontalDivider()
                }
                items(history) { roadmap ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = roadmap.skill.toCase(), fontSize = 18.sp)
                        },
                        selected = state.roadmapPrompt.id == roadmap.roadmapId,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            viewModel.updateStateFromHistory(roadmap)
                        }
                    )
                }
            }
        }
    }, drawerState = drawerState) {
        Scaffold(topBar = {
            TopAppBar(title = {
                Text(text = "Roadmap Creator")
            }, navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Drawer")
                }
            })
        }) { paddingValues ->
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (state.response.isEmpty()) {
                    item("field") {
                        RoadmapInput(viewModel)
                    }
                } else {
                    item("Graph Item") {
                        Text(
                            text = viewModel.roadmapPrompt.skill.toCase(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                )
                                .clickable {
                                    navController.navigate(Screens.GraphScreen(state.response))
                                }, shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${viewModel.roadmapPrompt.skill} roadmap".toCase(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Log.d("HomeScreen", "Response: ${state.response}")
                        Text(text = state.response)
                    }
                }
                if (state.loading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                item {

                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(graph: String, navController: NavController) {
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded, skipHiddenState = false
        )
    )
    Column(
        Modifier
            .systemBarsPadding()
            .padding(top = 12.dp)) {
        BottomSheetScaffold(sheetContent = {
            Mermaid(string = graph)
        }, sheetSwipeEnabled = false, sheetDragHandle = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = navController::popBackStack) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Close")
                }
            }
        }, scaffoldState = sheetState, modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
        ) {}
    }


}