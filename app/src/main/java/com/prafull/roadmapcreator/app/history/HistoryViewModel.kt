package com.prafull.roadmapcreator.app.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.roadmapcreator.app.data.RoadmapDao
import com.prafull.roadmapcreator.app.data.RoadmapEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent

class HistoryViewModel(
    private val dao: RoadmapDao
) : KoinComponent, ViewModel() {

    val history: StateFlow<List<RoadmapEntity>> = dao.getRoadmaps().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

}