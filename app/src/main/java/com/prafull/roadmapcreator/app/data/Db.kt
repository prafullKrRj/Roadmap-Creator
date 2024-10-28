package com.prafull.roadmapcreator.app.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [RoadmapEntity::class], version = 1, exportSchema = false)
abstract class RoadmapCreatorDB : RoomDatabase() {
    abstract fun roadmapDao(): RoadmapDao
}