package com.prafull.roadmapcreator.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prafull.roadmapcreator.app.home.FocusArea
import com.prafull.roadmapcreator.app.home.LearningStyle
import com.prafull.roadmapcreator.app.home.Level
import com.prafull.roadmapcreator.app.home.PrerequisiteKnowledge
import com.prafull.roadmapcreator.app.home.Timeframe

@Entity
data class RoadmapEntity(
    @PrimaryKey(autoGenerate = true)
    val roadmapPrimaryKey: Long = 0,
    val roadmapId: String,
    val skill: String,
    val level: String = Level.DEFAULT.name,
    val timeframe: String = Timeframe.NORMAL.name,
    val focusArea: String = FocusArea.DEFAULT.name,
    val prerequisiteKnowledge: String = PrerequisiteKnowledge.NONE.name,
    val learningStyle: String = LearningStyle.DEFAULT.name,
    val time: Long = System.currentTimeMillis(),
    val roadmap: String
)
