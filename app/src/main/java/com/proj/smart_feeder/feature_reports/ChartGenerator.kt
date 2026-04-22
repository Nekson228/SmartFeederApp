package com.proj.smart_feeder.feature_reports

import android.graphics.Bitmap
import android.graphics.Canvas
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

        val paint = android.graphics.Paint().apply {
            color = "#1e66f5".toColorInt()
            strokeWidth = 4f
            style = android.graphics.Paint.Style.STROKE
            isAntiAlias = true
        }

        val padding = 60f
        val usableWidth = width - padding * 2
        val usableHeight = height - padding * 2
        val maxVal = feedingStats.maxOrNull() ?: 1f
        val stepX = usableWidth / (feedingStats.size - 1).coerceAtLeast(1)

        val path = android.graphics.Path()
        feedingStats.forEachIndexed { index, value ->
            val x = padding + index * stepX
            val y = height - padding - (value / maxVal * usableHeight)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, paint)

        // Draw Axes
        val axisPaint = android.graphics.Paint().apply {
            color = "#4c4f69".toColorInt()
            strokeWidth = 2f
        }
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint)
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
