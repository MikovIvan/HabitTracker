package ru.mikov.domain.models

data class Habit(
    val color: Int,
    val count: Int,
    val date: Int,
    val description: String,
    val doneDates: List<Int> = emptyList(),
    val frequency: Int,
    val priority: Int,
    val title: String,
    val type: Int,
    val uid: String,
    val isSynchronized: Boolean = false
)

enum class HabitTypeDomain {
    GOOD,
    BAD
}