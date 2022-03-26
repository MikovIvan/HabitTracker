package ru.mikov.habittracker.data.local

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.mikov.habittracker.App


const val SELECTED_TAB = "SelectedTab"

object PrefManager {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun saveSelectedTab(tab: Int) {
        preferences.edit().apply {
            putInt(SELECTED_TAB, tab)
            apply()
        }
    }

    fun getSelectedTab(): Int {
        return preferences.getInt(SELECTED_TAB, 0)
    }
}