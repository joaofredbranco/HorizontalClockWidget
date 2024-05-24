package com.example.horizontalclockwidget

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object PreferencesManager {

    private const val PREFS_NAME = "horizontal_clock_prefs"
    private const val KEY_BAR_COLOR = "key_bar_color"
    private const val KEY_START_TIME = "key_start_time"
    private const val KEY_END_TIME = "key_end_time"
    private const val KEY_INTERVAL = "key_interval"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveBarColor(context: Context, color: Int) {
        getPreferences(context).edit().putInt(KEY_BAR_COLOR, color).apply()
        Log.d("PreferencesManager", "saveBarColor: $color")
    }

    fun getBarColor(context: Context): Int {
        val color = getPreferences(context).getInt(KEY_BAR_COLOR, -0x10000) // Default color red
        Log.d("PreferencesManager", "getBarColor: $color")
        return color
    }

    fun saveStartTime(context: Context, startTime: Int) {
        getPreferences(context).edit().putInt(KEY_START_TIME, startTime).apply()
        Log.d("PreferencesManager", "saveStartTime: $startTime")
    }

    fun getStartTime(context: Context): Int {
        val startTime = getPreferences(context).getInt(KEY_START_TIME, 0)
        Log.d("PreferencesManager", "getStartTime: $startTime")
        return startTime
    }

    fun saveEndTime(context: Context, endTime: Int) {
        getPreferences(context).edit().putInt(KEY_END_TIME, endTime).apply()
        Log.d("PreferencesManager", "saveEndTime: $endTime")
    }

    fun getEndTime(context: Context): Int {
        val endTime = getPreferences(context).getInt(KEY_END_TIME, 24)
        Log.d("PreferencesManager", "getEndTime: $endTime")
        return endTime
    }

    fun saveInterval(context: Context, interval: Int) {
        getPreferences(context).edit().putInt(KEY_INTERVAL, interval).apply()
        Log.d("PreferencesManager", "saveInterval: $interval")
    }

    fun getInterval(context: Context): Int {
        val interval = getPreferences(context).getInt(KEY_INTERVAL, 1)
        Log.d("PreferencesManager", "getInterval: $interval")
        return interval
    }
}
