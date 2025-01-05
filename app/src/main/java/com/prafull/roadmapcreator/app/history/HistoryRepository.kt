package com.prafull.roadmapcreator.app.history

import com.prafull.roadmapcreator.app.data.RoadmapEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {


    fun getHistory(): Flow<List<RoadmapEntity>>
    suspend fun deleteHistory(roadmapEntity: RoadmapEntity)
    suspend fun deleteAllHistory()
}