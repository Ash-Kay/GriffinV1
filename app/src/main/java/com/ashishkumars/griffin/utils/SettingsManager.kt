package com.ashishkumars.griffin.utils

import android.content.Context
import com.ashishkumars.griffin.BuildConfig

class SettingsManager(private val context: Context) {

    //In Seconds
    fun getDelay(): Int {
        return SharedPreferenceManager.getIntData(context, Constants.KEY_TIME_DELAY)
    }

    fun setDelay(delay: Int) {
        SharedPreferenceManager.saveData(context, Constants.KEY_TIME_DELAY, delay)
    }

    //In Seconds
    fun getEdgeTimerDuration(): Int {
        return SharedPreferenceManager.getIntData(context, Constants.KEY_EDGE_TIMER_DURATION)
    }

    fun setEdgeTimerDuration(duration: Int) {
        SharedPreferenceManager.saveData(context, Constants.KEY_EDGE_TIMER_DURATION, duration)
    }

    fun getIsDebugMode(): Boolean {
        if (!BuildConfig.DEBUG) return false
        return SharedPreferenceManager.getBooleanData(context, Constants.KEY_DEBUG_MODE)
    }

    fun setIsDebugMode(isDebugMode: Boolean) {
        SharedPreferenceManager.saveData(context, Constants.KEY_DEBUG_MODE, isDebugMode)
    }
}