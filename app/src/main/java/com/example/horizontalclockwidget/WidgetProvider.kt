package com.example.horizontalclockwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import java.util.*

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("WidgetProvider", "onUpdate called")
        for (appWidgetId in appWidgetIds) {
            Log.d("WidgetProvider", "Updating widget ID: $appWidgetId")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        setAlarm(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        // Cancel the alarm when the last widget is disabled
        cancelAlarm(context)
    }

    private fun setAlarm(context: Context) {
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = ACTION_UPDATE_TIME
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            60000, // Update every minute
            pendingIntent
        )
    }

    private fun cancelAlarm(context: Context?) {
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = ACTION_UPDATE_TIME
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_UPDATE_TIME -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, WidgetProvider::class.java)
                )
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            AppWidgetManager.ACTION_APPWIDGET_CONFIGURE -> {
                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val configIntent = Intent(context, WidgetConfigActivity::class.java)
                    configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    configIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(configIntent)
                }
            }
        }
    }

    companion object {
        const val ACTION_UPDATE_TIME = "com.example.horizontalclockwidget.UPDATE_TIME"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            Log.d("WidgetProvider", "updateAppWidget called for widgetId: $appWidgetId")
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val prefs = context.getSharedPreferences(WidgetConfigActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val startTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + appWidgetId + "_startTime", 0)
            val endTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + appWidgetId + "_endTime", 24)
            val barColor = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + appWidgetId + "_barColor", Color.RED)

            Log.d("WidgetProvider", "updateAppWidget - startTime: $startTime, endTime: $endTime, barColor: $barColor")

            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentTotalMinutes = currentHour * 60 + currentMinute

            val startTotalMinutes = startTime * 60
            val endTotalMinutes = endTime * 60
            val totalTime = endTotalMinutes - startTotalMinutes
            val elapsedTime = currentTotalMinutes - startTotalMinutes
            val percentage = (elapsedTime.toFloat() / totalTime)

            val barViewWidth = context.resources.getDimensionPixelSize(R.dimen.widget_bar_width)
            val indicatorPositionPx = (barViewWidth * percentage).toInt()

            views.setViewVisibility(R.id.fill_view, View.VISIBLE)
            views.setInt(R.id.fill_view, "setWidth", barViewWidth)
            views.setInt(R.id.fill_view, "setBackgroundColor", Color.TRANSPARENT)

            views.setInt(R.id.indicator, "setBackgroundColor", barColor)
            views.setFloat(R.id.indicator, "setTranslationX", indicatorPositionPx.toFloat())

            views.setInt(R.id.indicator2, "setBackgroundColor", barColor)
            views.setFloat(R.id.indicator2, "setTranslationX", indicatorPositionPx.toFloat())

            views.setInt(R.id.indicator3, "setBackgroundColor", barColor)
            views.setFloat(R.id.indicator3, "setTranslationX", indicatorPositionPx.toFloat())

            views.setTextViewText(R.id.start_time, "$startTime h")
            views.setTextViewText(R.id.end_time, "$endTime h")

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
