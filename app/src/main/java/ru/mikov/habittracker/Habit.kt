package ru.mikov.habittracker

data class Habit(
    val name: String,
    val description: String,
    val priority: Priority,
    val type: String,
    val periodicity: String,
    val color: String
)

enum class Priority {
    LOW, MEDIUM, HIGH
}