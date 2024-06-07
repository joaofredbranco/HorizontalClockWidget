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
import android.view.View
import java.util.*

class WidgetService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("WidgetService", "onStartCommand called")

        val widgetManager = AppWidgetManager.getInstance(this)

        // Update the first widget type
        updateWidgets(
            widgetManager,
            ComponentName(this, WidgetProvider::class.java),
            R.layout.widget_layout
        ) { context, views, prefs, widgetId ->
            val startTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_startTime", 0)
            val endTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_endTime", 24)
            val barColor = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + widgetId + "_barColor", Color.RED)

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
            views.setInt(R.id.fill_view, "setBackgroundColor", Color.TRANSPARENT) // Making fill_view transparent

            views.setInt(R.id.indicator, "setBackgroundColor", barColor)
            views.setFloat(R.id.indicator, "setTranslationX", indicatorPositionPx.toFloat())

            views.setInt(R.id.indicator2, "setBackgroundColor", barColor)
            views.setFloat(R.id.indicator2, "setTranslationX", indicatorPositionPx.toFloat())

            views.setInt(R.id.indicator3, "setBackgroundColor", barColor)
            views.setFloat(R.id.indicator3, "setTranslationX", indicatorPositionPx.toFloat())

            views.setTextViewText(R.id.start_time, "$startTime h")
            views.setTextViewText(R.id.end_time, "$endTime h")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateWidgets(
        widgetManager: AppWidgetManager,
        componentName: ComponentName,
        layoutId: Int,
        updateViews: (context: Context, views: RemoteViews, prefs: android.content.SharedPreferences, widgetId: Int) -> Unit
    ) {
        val allWidgetIds = widgetManager.getAppWidgetIds(componentName)
        for (widgetId in allWidgetIds) {
            val views = RemoteViews(packageName, layoutId)
            val prefs = getSharedPreferences(WidgetConfigActivity.PREFS_NAME, Context.MODE_PRIVATE)
            updateViews(this, views, prefs, widgetId)
            widgetManager.updateAppWidget(widgetId, views)
            Log.d("WidgetService", "App widget updated: $widgetId")
        }
    }
}
