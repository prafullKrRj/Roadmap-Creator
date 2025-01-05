package com.prafull.roadmapcreator.app

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.prafull.roadmapcreator.app.data.RoadmapDao
import com.prafull.roadmapcreator.app.data.RoadmapEntity
import com.prafull.roadmapcreator.app.home.FocusArea
import com.prafull.roadmapcreator.app.home.LearningStyle
import com.prafull.roadmapcreator.app.home.Level
import com.prafull.roadmapcreator.app.home.PrerequisiteKnowledge
import com.prafull.roadmapcreator.app.home.Timeframe
import com.prafull.roadmapcreator.utils.ApiKey
import com.prafull.roadmapcreator.utils.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID


data class RoadmapPrompt(
    val id: String = UUID.randomUUID().toString(),
    val skill: String,
    val level: Level = Level.DEFAULT,
    val timeframe: Timeframe = Timeframe.NORMAL,
    val focusArea: FocusArea = FocusArea.DEFAULT,
    val prerequisiteKnowledge: PrerequisiteKnowledge = PrerequisiteKnowledge.NONE,
    val learningStyle: LearningStyle = LearningStyle.DEFAULT,
) {
    fun toPrompt(): String {
        return """
            skill: $skill
            level: $level
            timeframe: $timeframe
            focus_area: $focusArea
            prerequisite_knowledge: $prerequisiteKnowledge
            learning_style: $learningStyle
        """.trimIndent()
    }
}

data class UiState(
    val prompt: String,
    val roadmapPrompt: RoadmapPrompt,
    val response: String,
    val loading: Boolean,
    val error: Pair<Boolean, String?>
)

sealed class NetworkState {
    data object Idle : NetworkState()
    data object Loading : NetworkState()
    data object Success : NetworkState()
    data class Error(val message: String) : NetworkState()
}

class AppViewModel(
    private val dao: RoadmapDao,
    private val apiKey: ApiKey
) : ViewModel(), KoinComponent {

    var roadmapPrompt by mutableStateOf(
        RoadmapPrompt(
            skill = ""
        )
    )
    private val _state = MutableStateFlow(UiState("", roadmapPrompt, "", false, Pair(false, null)))


    val state = _state.asStateFlow()

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()


    private val generation = GenerativeModel(
        modelName = "gemini-2.0-flash-exp",
        apiKey = apiKey.apiKey,
        systemInstruction = content {
            text(Const.FLOWCHART_SYSTEM_PROMPT)
        })
    private val chat = generation.startChat()

    fun sendPrompt() {
        _networkState.update {
            NetworkState.Loading
        }
        viewModelScope.launch {
            try {
                _state.value = UiState(
                    prompt = roadmapPrompt.toPrompt(),
                    response = "",
                    roadmapPrompt = roadmapPrompt,
                    loading = true,
                    error = Pair(false, null)
                )
                val response = chat.sendMessage(roadmapPrompt.toPrompt())
                response.text?.let { text ->
                    _state.value = UiState(
                        prompt = roadmapPrompt.toPrompt(),
                        response = text.formatRoadmap(),
                        loading = false,
                        roadmapPrompt = roadmapPrompt,
                        error = Pair(false, null)
                    )
                }
                response.text?.formatRoadmap()?.let {
                    RoadmapEntity(
                        skill = roadmapPrompt.skill,
                        level = roadmapPrompt.level.name,
                        timeframe = roadmapPrompt.timeframe.name,
                        focusArea = roadmapPrompt.focusArea.name,
                        prerequisiteKnowledge = roadmapPrompt.prerequisiteKnowledge.name,
                        learningStyle = roadmapPrompt.learningStyle.name,
                        roadmap = it,
                        roadmapId = roadmapPrompt.id
                    )
                }?.let {
                    dao.insertRoadmap(
                        it
                    )
                }
                Log.d("testing", response.text ?: "")
                _networkState.update {
                    NetworkState.Success
                }
            } catch (e: Exception) {
                _state.value = UiState(
                    prompt = roadmapPrompt.toPrompt(),
                    response = "",
                    roadmapPrompt = roadmapPrompt,
                    loading = false,
                    error = Pair(true, e.message)
                )
                Log.d("testing", e.message.toString())
                e.printStackTrace()
                _networkState.update {
                    NetworkState.Error(e.message ?: "An error occurred")
                }
            }
        }
    }

    fun resetState() {
        _networkState.value = NetworkState.Idle
    }

    private fun String.formatRoadmap(): String {
        this.substringAfter("```mermaid").let {
            return it.substringBeforeLast("```")
        }
    }
}

const val dummyResponse = "graph TD\n" +
        "    Start([Start Here]) --> Prerequisites\n" +
        "\n" +
        "    %% Level 1 - Prerequisites\n" +
        "    Prerequisites --> Languages\n" +
        "    Languages --> Java[Java Fundamentals]\n" +
        "    Languages --> Kotlin[Kotlin Basics]\n" +
        "    Prerequisites --> VersionControl[Git Basics]\n" +
        "    Prerequisites --> CommandLine[Command Line Usage]\n" +
        "\n" +
        "    %% Level 2 - Android Studio & Basics\n" +
        "    Java & Kotlin --> AndroidStudio\n" +
        "    AndroidStudio --> ProjectSetup[Project Setup]\n" +
        "    AndroidStudio --> BuildSystem[Gradle Basics]\n" +
        "    \n" +
        "    %% Level 3 - Core Concepts\n" +
        "    ProjectSetup --> CoreConcepts\n" +
        "    CoreConcepts --> ActivityLifecycle\n" +
        "    CoreConcepts --> Fragments\n" +
        "    CoreConcepts --> Intents\n" +
        "    \n" +
        "    %% Level 4 - UI Development\n" +
        "    CoreConcepts --> UIBasics\n" +
        "    UIBasics --> Layouts[XML Layouts]\n" +
        "    UIBasics --> BasicViews[Common Views]\n" +
        "    UIBasics --> ViewBinding\n" +
        "    \n" +
        "    %% Level 5 - Advanced UI\n" +
        "    UIBasics --> AdvancedUI\n" +
        "    AdvancedUI --> RecyclerView\n" +
        "    AdvancedUI --> MaterialDesign\n" +
        "    AdvancedUI --> JetpackCompose\n" +
        "    \n" +
        "    %% Level 6 - Data Management\n" +
        "    AdvancedUI --> DataManagement\n" +
        "    DataManagement --> SharedPreferences\n" +
        "    DataManagement --> SQLite\n" +
        "    DataManagement --> RoomDatabase\n" +
        "    \n" +
        "    %% Level 7 - Networking\n" +
        "    DataManagement --> Networking\n" +
        "    Networking --> Retrofit\n" +
        "    Networking --> OkHttp\n" +
        "    Networking --> APIs[REST APIs]\n" +
        "    \n" +
        "    %% Level 8 - Architecture\n" +
        "    Networking --> Architecture\n" +
        "    Architecture --> MVVM\n" +
        "    Architecture --> CleanArch[Clean Architecture]\n" +
        "    Architecture --> DependencyInjection\n" +
        "    DependencyInjection --> Hilt\n" +
        "    \n" +
        "    %% Level 9 - Advanced Concepts\n" +
        "    Architecture --> AdvancedConcepts\n" +
        "    AdvancedConcepts --> Coroutines\n" +
        "    AdvancedConcepts --> Flow\n" +
        "    AdvancedConcepts --> WorkManager\n" +
        "    \n" +
        "    %% Level 10 - Testing\n" +
        "    AdvancedConcepts --> Testing\n" +
        "    Testing --> UnitTesting\n" +
        "    Testing --> UITesting\n" +
        "    Testing --> IntegrationTests\n" +
        "    \n" +
        "    %% Level 11 - Publishing\n" +
        "    Testing --> Publishing\n" +
        "    Publishing --> AppSigning\n" +
        "    Publishing --> PlayStore[Play Store Console]\n" +
        "    Publishing --> AppBundle\n" +
        "    \n" +
        "    %% Styling\n" +
        "    classDef phase1 fill:#e6f3ff,stroke:#4a90e2,stroke-width:2px\n" +
        "    classDef phase2 fill:#f9e6ff,stroke:#9b51e0,stroke-width:2px\n" +
        "    classDef phase3 fill:#ffe6e6,stroke:#e04646,stroke-width:2px\n" +
        "    \n" +
        "    class Start,Prerequisites,Languages,Java,Kotlin,VersionControl,CommandLine,AndroidStudio,ProjectSetup,BuildSystem phase1\n" +
        "    class CoreConcepts,UIBasics,AdvancedUI,DataManagement,Networking phase2\n" +
        "    class Architecture,AdvancedConcepts,Testing,Publishing phase3"