package ru.mikov.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

const val IS_DB_EMPTY = "IS_DB_EMPTY"

class PrefManager(context: Context) {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
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