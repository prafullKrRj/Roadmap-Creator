package com.prafull.roadmapcreator.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prafull.roadmapcreator.R
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Roadmap Creator")
            })
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item("field") {
                InputField(viewModel)
            }
            item {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(viewModel: AppViewModel) {
    var skill by remember { mutableStateOf("") }
    var expandedMenu by remember { mutableStateOf<String?>(null) }
    var expandCard by rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Create Your Learning Roadmap",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                // Skill Input
                OutlinedTextField(
                    value = skill,
                    onValueChange = { skill = it },
                    label = { Text("Skill to Learn") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.baseline_school_24),
                            contentDescription = null
                        )
                    }
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = {
                        expandCard = !expandCard
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand",
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(if (expandCard) 180f else 0f)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = expandCard,
                    enter = fadeIn() + expandVertically(
                        animationSpec = tween(
                            durationMillis = 300
                        )
                    ),
                    exit = fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Level Selection
                        ExposedDropdownMenuBox(
                            expanded = expandedMenu == "level",
                            onExpandedChange = {
                                expandedMenu = if (expandedMenu == "level") null else "level"
                            }
                        ) {
                            OutlinedTextField(
                                value = viewModel.roadmapPrompt.level?.name ?: "Select Level",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu == "level") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        ImageVector.vectorResource(id = R.drawable.baseline_grade_24),
                                        contentDescription = null
                                    )
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMenu == "level",
                                onDismissRequest = { expandedMenu = null }
                            ) {
                                Level.entries.forEach { level ->
                                    DropdownMenuItem(
                                        text = { Text(level.name.toLowerCase().capitalize()) },
                                        onClick = {
                                            viewModel.roadmapPrompt = viewModel.roadmapPrompt.copy(
                                                level = level
                                            )
                                            expandedMenu = null
                                        }
                                    )
                                }
                            }
                        }

                        // Timeframe Selection
                        ExposedDropdownMenuBox(
                            expanded = expandedMenu == "timeframe",
                            onExpandedChange = {
                                expandedMenu =
                                    if (expandedMenu == "timeframe") null else "timeframe"
                            }
                        ) {
                            OutlinedTextField(
                                value = viewModel.roadmapPrompt.timeframe?.name?.replace("_", " ")
                                    ?.toLowerCase()
                                    ?.capitalize() ?: "Select Timeframe",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu == "timeframe") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        ImageVector.vectorResource(id = R.drawable.baseline_timer_24),
                                        contentDescription = null
                                    )
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMenu == "timeframe",
                                onDismissRequest = { expandedMenu = null }
                            ) {
                                Timeframe.entries.forEach { timeframe ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                timeframe.name.replace("_", " ").toLowerCase()
                                                    .capitalize()
                                            )
                                        },
                                        onClick = {
                                            viewModel.roadmapPrompt = viewModel.roadmapPrompt.copy(
                                                timeframe = timeframe
                                            )
                                            expandedMenu = null
                                        }
                                    )
                                }
                            }
                        }

                        // Focus Area Selection with Radio Buttons
                        Column {
                            Text(
                                text = "Focus Area",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            FocusArea.entries.forEach { focus ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = viewModel.roadmapPrompt.focusArea == focus,
                                            onClick = {
                                                viewModel.roadmapPrompt =
                                                    viewModel.roadmapPrompt.copy(
                                                        focusArea = focus
                                                    )
                                            }
                                        )
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = viewModel.roadmapPrompt.focusArea == focus,
                                        onClick = {
                                            viewModel.roadmapPrompt = viewModel.roadmapPrompt.copy(
                                                focusArea = focus
                                            )
                                        }
                                    )
                                    Text(
                                        text = focus.name.toLowerCase().capitalize(),
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        // Prerequisite Knowledge
                        ExposedDropdownMenuBox(
                            expanded = expandedMenu == "prerequisite",
                            onExpandedChange = {
                                expandedMenu =
                                    if (expandedMenu == "prerequisite") null else "prerequisite"
                            }
                        ) {
                            OutlinedTextField(
                                value = viewModel.roadmapPrompt.prerequisiteKnowledge?.name?.replace(
                                    "_",
                                    " "
                                )
                                    ?.lowercase(Locale.getDefault())
                                    ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                    ?: "Select Prerequisites",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu == "prerequisite") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null
                                    )
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMenu == "prerequisite",
                                onDismissRequest = { expandedMenu = null }
                            ) {
                                PrerequisiteKnowledge.entries.forEach { prerequisite ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                prerequisite.name.toLowerCase(Locale.ROOT)
                                                    .capitalize(java.util.Locale.ROOT)
                                            )
                                        },
                                        onClick = {
                                            viewModel.roadmapPrompt = viewModel.roadmapPrompt.copy(
                                                prerequisiteKnowledge = prerequisite
                                            )
                                            expandedMenu = null
                                        }
                                    )
                                }
                            }
                        }

                        // Learning Style Selection with Chips
                        Column {
                            Text(
                                text = "Learning Style",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LearningStyle.values().forEach { style ->
                                    FilterChip(
                                        selected = viewModel.roadmapPrompt.learningStyle == style,
                                        onClick = {
                                            viewModel.roadmapPrompt = viewModel.roadmapPrompt.copy(
                                                learningStyle = style
                                            )
                                        },
                                        label = {
                                            Text(
                                                style.name.replace("_", " ").toLowerCase()
                                                    .capitalize()
                                            )
                                        },
                                        leadingIcon = {
                                            if (viewModel.roadmapPrompt.learningStyle == style) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }


                        Button(
                            onClick = {
                                // todo
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Generate Roadmap")
                        }
                    }
                }
            }
        }
    }
}