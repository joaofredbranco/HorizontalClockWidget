package com.example.horizontalclockwidget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

class HorizontalClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var startTime: Int = 0
    private var endTime: Int = 24
    private var interval: Int = 1
    private var barColor: Int = Color.BLUE
    private var step: Int = 1 // Valor padrão positivo
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
    }
    private val markerPaint = Paint().apply {
        color = barColor
        strokeWidth = 20f
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
    }

    fun setStep(newStep: Int) {
        if (newStep <= 0) {
            throw IllegalArgumentException("Step must be positive, was: $newStep.")
        }
        step = newStep
        Log.d("HorizontalClockView", "Step set to: $step")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()

        Log.d("HorizontalClockView", "onDraw called, width: $width, height: $height")

        if (step <= 0) {
            throw IllegalArgumentException("Step must be positive, was: $step.")
        }

        val padding = 16f
        canvas.drawLine(padding, height / 2, width - padding, height / 2, paint)
        Log.d("HorizontalClockView", "Drew horizontal line from $padding to ${width - padding}")

        for (i in startTime..endTime step interval) {
            val x = padding + (i - startTime) * ((width - 2 * padding) / (endTime - startTime))
            canvas.drawLine(x, height / 2 - 20, x, height / 2 + 20, paint)
            Log.d("HorizontalClockView", "Drew marker at: $x for hour: $i")
        }

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute
        val startTotalMinutes = startTime * 60
        val endTotalMinutes = endTime * 60
        val totalTime = endTotalMinutes - startTotalMinutes
        val elapsedTime = currentTotalMinutes - startTotalMinutes
        val percentage = (elapsedTime.toFloat() / totalTime) * 100
        val markerX = padding + (percentage / 100) * (width - 2 * padding)

        canvas.drawLine(markerX, height / 2 - 30, markerX, height / 2 + 30, markerPaint)
        Log.d("HorizontalClockView", "Drew current time marker at: $markerX for time percentage: $percentage")

        canvas.drawText("$startTime h", padding, height / 2 + 60, textPaint)
        canvas.drawText("$endTime h", width - textPaint.measureText("$endTime h") - padding, height / 2 + 60, textPaint)
        Log.d("HorizontalClockView", "Drew start time text at: $padding, drew end time text at: ${width - textPaint.measureText("$endTime h") - padding}")
    }

    fun setTimeConfig(startTime: Int, endTime: Int, interval: Int, barColor: Int) {
        this.startTime = startTime
        this.endTime = endTime
        this.interval = interval
        this.barColor = barColor
        markerPaint.color = barColor
        Log.d("HorizontalClockView", "setTimeConfig called with startTime: $startTime, endTime: $endTime, interval: $interval, barColor: $barColor")
        invalidate()
    }
}
