package com.prafull.roadmapcreator.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.prafull.roadmapcreator.app.data.RoadmapDao
import com.prafull.roadmapcreator.app.data.RoadmapEntity
import com.prafull.roadmapcreator.utils.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

class AppViewModel(
    private val dao: RoadmapDao
) : ViewModel() {

    val history: StateFlow<List<RoadmapEntity>> = dao.getallroadmaps().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    var roadmapPrompt by mutableStateOf(
        RoadmapPrompt(
            skill = ""
        )
    )
    private val _state = MutableStateFlow(UiState("", roadmapPrompt, "", false, Pair(false, null)))


    val state = _state.asStateFlow()


    private val generation = GenerativeModel(
        modelName = "gemini-1.5-flash-8b-001",
        apiKey = Const.API_KEY,
        systemInstruction = content {
            text(Const.FLOWCHART_SYSTEM_PROMPT)
        })
    private val chat = generation.startChat()

    fun sendPrompt() {
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
            } catch (e: Exception) {
                _state.value = UiState(
                    prompt = roadmapPrompt.toPrompt(),
                    response = "",
                    roadmapPrompt = roadmapPrompt,
                    loading = false,
                    error = Pair(true, e.message)
                )
                e.printStackTrace()
            }
        }
    }

    fun String.formatRoadmap(): String {
        this.substringAfter("```mermaid").let {
            return it.substringBeforeLast("```")
        }
    }

    fun clearState() {
        roadmapPrompt = RoadmapPrompt(
            skill = ""
        )
        _state.value = UiState("", roadmapPrompt, "", false, Pair(false, null))
    }

    fun updateStateFromHistory(roadmap: RoadmapEntity) {
        roadmapPrompt = RoadmapPrompt(
            id = roadmap.roadmapId,
            skill = roadmap.skill,
            level = Level.valueOf(roadmap.level),
            timeframe = Timeframe.valueOf(roadmap.timeframe),
            focusArea = FocusArea.valueOf(roadmap.focusArea),
            prerequisiteKnowledge = PrerequisiteKnowledge.valueOf(roadmap.prerequisiteKnowledge),
            learningStyle = LearningStyle.valueOf(roadmap.learningStyle)
        )
        _state.value = UiState(
            prompt = roadmapPrompt.toPrompt(),
            response = roadmap.roadmap,
            roadmapPrompt = roadmapPrompt,
            loading = false,
            error = Pair(false, null)
        )
    }
}