package com.prafull.roadmapcreator.app.history

import com.prafull.roadmapcreator.app.data.RoadmapDao
import com.prafull.roadmapcreator.app.data.RoadmapEntity
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class HistoryRepositoryImpl(
    private val dao: RoadmapDao
) : HistoryRepository, KoinComponent {
    override fun getHistory(): Flow<List<RoadmapEntity>> = dao.getRoadmaps()

    override suspend fun deleteHistory(roadmapEntity: RoadmapEntity) =
        dao.deleteRoadmap(roadmapEntity)

    override suspend fun deleteAllHistory() = dao.deleteAllRoadmaps()

}