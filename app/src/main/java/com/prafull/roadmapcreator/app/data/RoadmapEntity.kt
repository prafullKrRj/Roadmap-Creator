package com.prafull.roadmapcreator.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prafull.roadmapcreator.app.FocusArea
import com.prafull.roadmapcreator.app.LearningStyle
import com.prafull.roadmapcreator.app.Level
import com.prafull.roadmapcreator.app.PrerequisiteKnowledge
import com.prafull.roadmapcreator.app.Timeframe

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
