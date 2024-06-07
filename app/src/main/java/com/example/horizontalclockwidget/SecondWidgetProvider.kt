package com.example.horizontalclockwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import java.util.*

class SecondWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("SecondWidgetProvider", "onUpdate called")
        for (appWidgetId in appWidgetIds) {
            Log.d("SecondWidgetProvider", "Updating widget ID: $appWidgetId")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, SecondWidgetProvider::class.java)
                )
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            AppWidgetManager.ACTION_APPWIDGET_CONFIGURE -> {
                val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val configIntent = Intent(context, WidgetConfigActivity::class.java)
                    configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    configIntent.putExtra("isSecondWidget", true)
                    configIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(configIntent)
                }
            }
            ACTION_UPDATE_TIME -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, SecondWidgetProvider::class.java)
                )
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    companion object {
        private const val ACTION_UPDATE_TIME = "com.example.horizontalclockwidget.SECOND_WIDGET_UPDATE_TIME"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            Log.d("SecondWidgetProvider", "updateAppWidget called for widgetId: $appWidgetId")
            val views = RemoteViews(context.packageName, R.layout.widget_percentage)

            val prefs = context.getSharedPreferences(WidgetConfigActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val startTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + appWidgetId + "_second_startTime", 0)
            val endTime = prefs.getInt(WidgetConfigActivity.PREF_PREFIX_KEY + appWidgetId + "_second_endTime", 24)

            Log.d("SecondWidgetProvider", "updateAppWidget - startTime: $startTime, endTime: $endTime")

            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentTotalMinutes = currentHour * 60 + currentMinute

            val startTotalMinutes = startTime * 60
            val endTotalMinutes = endTime * 60
            val totalTime = endTotalMinutes - startTotalMinutes
            val elapsedTime = currentTotalMinutes - startTotalMinutes
            val percentage = (elapsedTime.toFloat() / totalTime)

            Log.d("SecondWidgetProvider", "Total time: $totalTime")
            Log.d("SecondWidgetProvider", "Elapsed time: $elapsedTime")
            Log.d("SecondWidgetProvider", "Percentage: ${percentage * 100}")

            val displayPercentage = (percentage * 100).toInt().toString() + "%"
            views.setTextViewText(R.id.textViewPercentage, displayPercentage)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun createUpdateTimeIntent(context: Context): Intent {
            return Intent(context, SecondWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_TIME
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }
}
