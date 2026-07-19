package com.happylens.ai_bmi_calculator.domain.model

enum class InsightKind {
    TREND,
    GOAL,
    SUGGESTION,
    QUICK_CHECK
}

data class Insight(
    val title: String,
    val description: String,
    val category: String,
    val kind: InsightKind
)
