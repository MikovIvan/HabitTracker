package ru.mikov.habittracker.data.entities

import java.io.Serializable


data class Habit(
    val id: String,
    var name: String,
    var description: String,
    var priority: String,
    var type: Int,
    var periodicity: String,
    val color: Int,
    var numberOfExecutions: String
) : Serializable



