package com.ashishkumars.griffin.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {
    private const val PREF_APP = "com.ashishkumars.griffin.prefs"

    fun getData(context: Context, key: String): String {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
            .getString(key, "") ?: ""
    }

    fun getBooleanData(context: Context, key: String): Boolean {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
            .getString(key, "false").toBoolean()
    }

    fun getIntData(context: Context, key: String): Int {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
            .getString(key, "0")?.toIntOrNull() ?: 0
    }

    fun <T> saveData(context: Context, key: String, value: T) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit()
            .putString(key, value.toString()).apply()
    }

    fun clearData(context: Context, key: String?) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().remove(key).apply()
    }
}
