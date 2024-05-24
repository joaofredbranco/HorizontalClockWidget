package com.example.horizontalclockwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.graphics.Color
import android.util.Log

class WidgetService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("WidgetService", "onStartCommand called")

        val widgetManager = AppWidgetManager.getInstance(this)
        val thisWidget = ComponentName(this, WidgetProvider::class.java)
        val allWidgetIds = widgetManager.getAppWidgetIds(thisWidget)

        for (widgetId in allWidgetIds) {
            Log.d("WidgetService", "Updating widget ID: $widgetId")

            val views = RemoteViews(packageName, R.layout.widget_layout)

            val prefs = getSharedPreferences(WidgetConfigActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val startTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_startTime", 0)
            val endTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_endTime", 24)
            val interval = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_interval", 1)
            val lineColor = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_barColor", Color.BLACK)
            val indicatorColor = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_indicatorColor", Color.RED)

            Log.d("WidgetService", "startTime: $startTime, endTime: $endTime, interval: $interval, lineColor: $lineColor, indicatorColor: $indicatorColor")

            // Atualizar a barra de tempo
            configureTimeBar(views, lineColor, indicatorColor, startTime, endTime, interval)

            views.setTextViewText(R.id.start_time, "$startTime:00")
            views.setTextViewText(R.id.end_time, "$endTime:00")

            widgetManager.updateAppWidget(widgetId, views)
            Log.d("WidgetService", "App widget updated: $widgetId")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun configureTimeBar(views: RemoteViews, lineColor: Int, indicatorColor: Int, startTime: Int, endTime: Int, interval: Int) {
        val currentTime = System.currentTimeMillis()
        val totalMinutesInDay = (currentTime / (1000 * 60)).toInt() % 1440
        val totalMinutesInInterval = (endTime - startTime) * 60
        val currentPosition = ((totalMinutesInDay - (startTime * 60)).toFloat() / totalMinutesInInterval * 100).toInt()

        Log.d("WidgetService", "Current position: $currentPosition")

        views.setInt(R.id.widget_time_bar, "setBackgroundColor", lineColor)

        val intervalCount = (endTime - startTime) / interval
        val markerWidth = 100 / intervalCount
        for (i in 0..intervalCount) {
            val position = i * markerWidth
            Log.d("WidgetService", "Marker position: $position")

            val markerView = RemoteViews(packageName, R.layout.widget_marker)
            markerView.setInt(R.id.widget_marker, "setBackgroundColor", Color.BLACK)
            views.addView(R.id.markers_container, markerView)
        }

        val indicatorView = RemoteViews(packageName, R.layout.widget_indicator)
        indicatorView.setInt(R.id.indicator, "setBackgroundColor", indicatorColor)
        views.addView(R.id.markers_container, indicatorView)

        Log.d("WidgetService", "Time bar configured with lineColor: $lineColor, indicatorColor: $indicatorColor")
    }
}
