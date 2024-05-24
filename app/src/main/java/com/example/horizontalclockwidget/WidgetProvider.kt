package com.example.horizontalclockwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.horizontalclockwidget.PreferencesManager.getBarColor
import com.example.horizontalclockwidget.PreferencesManager.getEndTime
import com.example.horizontalclockwidget.PreferencesManager.getInterval
import com.example.horizontalclockwidget.PreferencesManager.getStartTime
import java.util.*

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("WidgetProvider", "onUpdate called")
        for (appWidgetId in appWidgetIds) {
            Log.d("WidgetProvider", "Updating widget ID: $appWidgetId")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val ACTION_UPDATE_TIME = "com.example.horizontalclockwidget.UPDATE_TIME"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            Log.d("WidgetProvider", "updateAppWidget called for widgetId: $appWidgetId")
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val startTime = getStartTime(context)
            val endTime = getEndTime(context)
            val interval = getInterval(context)
            val barColor = getBarColor(context)

            Log.d("WidgetProvider", "startTime: $startTime, endTime: $endTime, interval: $interval, barColor: $barColor")

            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentTotalMinutes = currentHour * 60 + currentMinute

            // Calculate the percentage of the day passed
            val startTotalMinutes = startTime * 60
            val endTotalMinutes = endTime * 60
            val totalTime = endTotalMinutes - startTotalMinutes
            val elapsedTime = currentTotalMinutes - startTotalMinutes
            val percentage = (elapsedTime.toFloat() / totalTime) * 100

            Log.d("WidgetProvider", "currentHour: $currentHour, totalTime: $totalTime, elapsedTime: $elapsedTime, percentage: $percentage")

            views.setInt(R.id.widget_time_bar, "setBackgroundColor", barColor)

            // Update the progress bar
            val widgetWidth = context.resources.displayMetrics.widthPixels
            val timeBarWidth = (percentage * widgetWidth / 100).toInt()
            views.setInt(R.id.widget_time_bar, "setWidth", timeBarWidth)

            // Add hour markers based on the interval
            views.removeAllViews(R.id.markers_container)
            for (i in startTime until endTime step interval) {
                val markerView = RemoteViews(context.packageName, R.layout.widget_marker)
                views.addView(R.id.markers_container, markerView)
            }

            // Update the start and end times
            views.setTextViewText(R.id.start_time, "$startTime h")
            views.setTextViewText(R.id.end_time, "$endTime h")

            // Add the indicator
            val indicatorView = RemoteViews(context.packageName, R.layout.widget_indicator)
            indicatorView.setInt(R.id.indicator, "setBackgroundColor", barColor)
            views.addView(R.id.markers_container, indicatorView)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun createUpdateTimeIntent(context: Context): Intent {
            return Intent(context, WidgetProvider::class.java).apply {
                action = ACTION_UPDATE_TIME
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_TIME) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, WidgetProvider::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}
