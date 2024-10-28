package com.prafull.roadmapcreator.app

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.prafull.roadmapcreator.utils.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


data class RoadmapPrompt(
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
    val response: String,
    val loading: Boolean,
    val error: Pair<Boolean, String?>
)

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState("", "", false, Pair(false, null)))
    val state = _state.asStateFlow()
    var roadmapPrompt by mutableStateOf(
        RoadmapPrompt(
            skill = ""
        )
    )
    private val generation = GenerativeModel(
        modelName = "gemini-1.5-flash-8b-001",
        apiKey = Const.API_KEY,
        systemInstruction = content {
            text(Const.FLOWCHART_SYSTEM_PROMPT)
        }
    )
    private val chat = generation.startChat()

    fun sendPrompt() {
        try {
            Log.d("AppViewModel", "sendPrompt: ${roadmapPrompt.toPrompt()}")
            _state.value = UiState(
                prompt = roadmapPrompt.toPrompt(),
                response = "",
                loading = true,
                error = Pair(false, null)
            )
            /* viewModelScope.launch {
                 val response = chat.sendMessage(prompt)
                 response.text?.let { text ->
                     state.update {
                         text.substringAfterLast("```mermaid").substringBeforeLast("```")
                     }
                     Log.d(
                         "AppViewModel",
                         "sendPrompt: ${
                             text.substringAfterLast("```mermaid").substringBeforeLast("```")
                         }"
                     )
                 }
             }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}