package com.example.horizontalclockwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class WidgetConfigActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_configure)

        Log.d("WidgetConfigActivity", "onCreate: Iniciando WidgetConfigActivity")

        setResult(RESULT_CANCELED)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val lineColorSpinner = findViewById<Spinner>(R.id.line_color_spinner)
        val startTimeSpinner = findViewById<Spinner>(R.id.start_time_spinner)
        val endTimeSpinner = findViewById<Spinner>(R.id.end_time_spinner)
        val saveButton = findViewById<Button>(R.id.save_button)

        Log.d("WidgetConfigActivity", "Verificando spinners e botão")
        if (lineColorSpinner == null) Log.e("WidgetConfigActivity", "lineColorSpinner is null")
        if (startTimeSpinner == null) Log.e("WidgetConfigActivity", "startTimeSpinner is null")
        if (endTimeSpinner == null) Log.e("WidgetConfigActivity", "endTimeSpinner is null")
        if (saveButton == null) Log.e("WidgetConfigActivity", "saveButton is null")

        Log.d("WidgetConfigActivity", "Configurando adapters para spinners")
        val lineColorAdapter = ArrayAdapter.createFromResource(this, R.array.line_colors, android.R.layout.simple_spinner_item)
        lineColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        lineColorSpinner.adapter = lineColorAdapter

        val startTimeAdapter = ArrayAdapter.createFromResource(this, R.array.start_times, android.R.layout.simple_spinner_item)
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startTimeSpinner.adapter = startTimeAdapter

        val endTimeAdapter = ArrayAdapter.createFromResource(this, R.array.end_times, android.R.layout.simple_spinner_item)
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        endTimeSpinner.adapter = endTimeAdapter
        endTimeSpinner.setSelection(getIndex(endTimeSpinner, "24:00"))

        Log.d("WidgetConfigActivity", "Spinners configurados")

        saveButton.setOnClickListener {
            Log.d("WidgetConfigActivity", "Botão 'Save' clicado")

            val context = this@WidgetConfigActivity

            val lineColor = when (lineColorSpinner.selectedItem.toString()) {
                "Red" -> Color.RED
                "Green" -> Color.GREEN
                "Blue" -> Color.BLUE
                "Black" -> Color.BLACK
                "White" -> Color.WHITE
                "Yellow" -> Color.YELLOW
                else -> Color.BLACK
            }
            val startTime = startTimeSpinner.selectedItem.toString().split(":")[0].toInt()
            val endTime = endTimeSpinner.selectedItem.toString().split(":")[0].toInt()

            // Save the values
            savePreferences(context, appWidgetId, lineColor, startTime, endTime)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            WidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    private fun savePreferences(context: Context, appWidgetId: Int, lineColor: Int, startTime: Int, endTime: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + "_barColor", lineColor)
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + "_startTime", startTime)
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + "_endTime", endTime)
        prefs.apply()
        Log.d("WidgetConfigActivity", "Preferences saved: barColor: $lineColor, startTime: $startTime, endTime: $endTime")
    }

    private fun getIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) {
                return i
            }
        }
        return 0
    }

    companion object {
        const val PREFS_NAME = "com.example.horizontalclockwidget.WidgetConfigActivity"
        const val PREF_PREFIX_KEY = "appwidget_"
    }
}
