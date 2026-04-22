package com.proj.smart_feeder.feature_reports

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Base64
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import java.io.ByteArrayOutputStream

object ChartGenerator {

    fun generateFeedingChartBase64(feedingStats: List<Float>): String {
        if (feedingStats.isEmpty()) return ""

        val width = 800f
        val height = 400f
        val bitmap = createBitmap(width.toInt(), height.toInt())
        val canvas = Canvas(bitmap)

        // Catppuccin Latte Base
        canvas.drawColor("#eff1f5".toColorInt())

        val paddingLeft = 100f
        val paddingRight = 40f
        val paddingTop = 40f
        val paddingBottom = 60f

        val usableWidth = width - paddingLeft - paddingRight
        val usableHeight = height - paddingTop - paddingBottom
        val maxVal = feedingStats.maxOrNull()?.coerceAtLeast(100f) ?: 100f
        val stepX = usableWidth / (feedingStats.size - 1).coerceAtLeast(1)

        // Paints
        val linePaint = Paint().apply {
            color = "#1e66f5".toColorInt()
            strokeWidth = 4f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val axisPaint = Paint().apply {
            color = "#4c4f69".toColorInt()
            strokeWidth = 2f
        }

        val textPaint = Paint().apply {
            color = "#4c4f69".toColorInt()
            textSize = 18f
            isAntiAlias = true
        }

        textPaint.textAlign = Paint.Align.RIGHT
        val yStepCount = 5
        for (i in 0..yStepCount) {
            val ratio = i.toFloat() / yStepCount
            val y = height - paddingBottom - (ratio * usableHeight)
            val value = (ratio * maxVal).toInt()

            canvas.drawLine(paddingLeft - 10f, y, paddingLeft, y, axisPaint)
            canvas.drawText("${value}г", paddingLeft - 15f, y + 6f, textPaint)
        }

        textPaint.textAlign = Paint.Align.CENTER
        feedingStats.forEachIndexed { index, _ ->
            val x = paddingLeft + index * stepX
            canvas.drawLine(x, height - paddingBottom, x, height - paddingBottom + 10f, axisPaint)
            canvas.drawText("${index + 1}", x, height - paddingBottom + 30f, textPaint)
        }

        val path = Path()
        feedingStats.forEachIndexed { index, value ->
            val x = paddingLeft + index * stepX
            val y = height - paddingBottom - (value / maxVal * usableHeight)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)

        canvas.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom, axisPaint) // Vertical
        canvas.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom, axisPaint) // Horizontal

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
