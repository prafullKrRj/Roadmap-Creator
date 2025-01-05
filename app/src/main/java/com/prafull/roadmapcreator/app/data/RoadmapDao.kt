package com.prafull.roadmapcreator.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadmapDao {
    @Upsert
    suspend fun insertRoadmap(roadmap: RoadmapEntity)

    @Query("SELECT * FROM RoadmapEntity")
    fun getRoadmaps(): Flow<List<RoadmapEntity>>

    @Delete
    suspend fun deleteRoadmap(roadmap: RoadmapEntity)

    @Query("DELETE FROM RoadmapEntity")
    suspend fun deleteAllRoadmaps()
}