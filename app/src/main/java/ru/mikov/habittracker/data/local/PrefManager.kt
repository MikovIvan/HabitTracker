package ru.mikov.habittracker.data.local

import android.content.SharedPreferences
import androidx.preference.PreferenceManager


import ru.mikov.habittracker.App

const val IS_DB_EMPTY = "IS_DB_EMPTY"

object PrefManager {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun saveDbStatus(isEmpty: Boolean) {
        preferences.edit().apply {
            putBoolean(IS_DB_EMPTY, isEmpty)
            apply()
        }
    }

    fun isDbEmpty(): Boolean {
        return preferences.getBoolean(IS_DB_EMPTY, true)
    }

    fun clearPrefs() {
        preferences.edit().apply {
            clear()
            apply()
        }
    }
}