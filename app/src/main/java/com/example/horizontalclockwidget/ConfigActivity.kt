package com.example.horizontalclockwidget

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class ConfigActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        Log.d("ConfigActivity", "onCreate: Iniciando ConfigActivity")

        val indicatorColorSpinner = findViewById<Spinner>(R.id.indicator_color_spinner)
        val startTimeSpinner = findViewById<Spinner>(R.id.start_time_spinner)
        val endTimeSpinner = findViewById<Spinner>(R.id.end_time_spinner)
        val intervalSpinner = findViewById<Spinner>(R.id.interval_spinner)
        val saveButton = findViewById<Button>(R.id.save_button)

        Log.d("ConfigActivity", "Verificando spinners e botão")
        if (indicatorColorSpinner == null) Log.e("ConfigActivity", "indicatorColorSpinner is null")
        if (startTimeSpinner == null) Log.e("ConfigActivity", "startTimeSpinner is null")
        if (endTimeSpinner == null) Log.e("ConfigActivity", "endTimeSpinner is null")
        if (intervalSpinner == null) Log.e("ConfigActivity", "intervalSpinner is null")
        if (saveButton == null) Log.e("ConfigActivity", "saveButton is null")

        Log.d("ConfigActivity", "Configurando adapters para spinners")
        val indicatorColorAdapter = ArrayAdapter.createFromResource(this, R.array.indicator_colors, android.R.layout.simple_spinner_item)
        indicatorColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        indicatorColorSpinner?.adapter = indicatorColorAdapter

        val startTimeAdapter = ArrayAdapter.createFromResource(this, R.array.start_times, android.R.layout.simple_spinner_item)
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startTimeSpinner?.adapter = startTimeAdapter

        val endTimeAdapter = ArrayAdapter.createFromResource(this, R.array.end_times, android.R.layout.simple_spinner_item)
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        endTimeSpinner?.adapter = endTimeAdapter
        endTimeSpinner?.setSelection(getIndex(endTimeSpinner, "24:00"))

        val intervalAdapter = ArrayAdapter.createFromResource(this, R.array.intervals, android.R.layout.simple_spinner_item)
        intervalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        intervalSpinner?.adapter = intervalAdapter

        Log.d("ConfigActivity", "Spinners configurados")

        saveButton?.setOnClickListener {
            Log.d("ConfigActivity", "Botão 'Save' clicado")

            val context = this@ConfigActivity

            val indicatorColor = when (indicatorColorSpinner?.selectedItem.toString()) {
                "Red" -> Color.RED
                "Green" -> Color.GREEN
                "Blue" -> Color.BLUE
                else -> Color.BLACK
            }
            val startTime = startTimeSpinner?.selectedItem.toString().split(":")[0].toInt()
            val endTime = endTimeSpinner?.selectedItem.toString().split(":")[0].toInt()
            val interval = intervalSpinner?.selectedItem.toString().toInt()

            if (startTime >= endTime) {
                // Show error message
                Log.e("ConfigActivity", "Start time must be less than end time.")
                return@setOnClickListener
            }

            Log.d("ConfigActivity", "Saving preferences: indicatorColor: $indicatorColor, startTime: $startTime, endTime: $endTime, interval: $interval")
            PreferencesManager.saveBarColor(context, indicatorColor)
            PreferencesManager.saveStartTime(context, startTime)
            PreferencesManager.saveEndTime(context, endTime)
            PreferencesManager.saveInterval(context, interval)

            val resultValue = Intent()
            setResult(RESULT_OK, resultValue)
            Log.d("ConfigActivity", "Preferences salvas, finalizando ConfigActivity")
            finish()
        }
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == myString) {
                return i
            }
        }
        return 0
    }
}
