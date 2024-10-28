package com.prafull.roadmapcreator.app

/*
*
*
* PARAMETERS:
1. level: [basic, intermediate, advanced]
2. timeframe: [1_month, 3_months, 6_months, 1_year]
3. focus_area: [practical, theoretical, both]
4. prerequisite_knowledge: [none, minimal, moderate, extensive]
5. learning_style: [structured, project_based, hybrid]
6. time_commitment: [1_hour_daily, 2_hours_daily, 4_hours_daily]
7. custom_requirements: (optional free text for specific user needs)

OUTPUT FORMAT:
1. Generate a TD (top-down) Mermaid flowchart
2. Use clear node descriptions (max 3 words per node)
3. Include time estimates for each major section
4. Group related concepts
5. Use consistent spacing for visual clarity
6. Maximum width: 400px
7. Optimize for portrait orientation
* */

enum class Level {
    DEFAULT, BASIC, INTERMEDIATE, ADVANCED
}

enum class Timeframe {
    NORMAL, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR
}

enum class FocusArea {
    DEFAULT, PRACTICAL, THEORETICAL, BOTH
}

enum class PrerequisiteKnowledge {
    NONE, MINIMAL, MODERATE, EXTENSIVE
}

enum class LearningStyle {
    DEFAULT, STRUCTURED, PROJECT_BASED, HYBRID
}