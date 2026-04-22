package com.proj.smart_feeder.feature_reports

import android.content.Context
import com.proj.smart_feeder.feature_profiles.domain.PetProfile

object HtmlReportBuilder {

    fun build(context: Context, profile: PetProfile, chartBase64: String): String {

        val template = context.assets.open("report_template.html")
            .bufferedReader()
            .use { it.readText() }

        val historyRowsHtml = if (profile.feedingHistory.isNotEmpty()) {
            profile.feedingHistory.joinToString("\n") { record ->
                val parts = record.split(" - ")
                "<tr><td>${parts.getOrNull(0) ?: record}</td><td>${parts.getOrNull(1) ?: ""}</td></tr>"
            }
        } else {
            "<tr><td colspan='2'>Нет данных</td></tr>"
        }

        val chartHtml = if (chartBase64.isNotEmpty()) {
            "<img src=\"data:image/png;base64,$chartBase64\" width=\"100%\" />"
        } else {
            "<p>Нет данных для графика</p>"
        }

        return template
            .replace("{{pet_name}}", profile.name)
            .replace("{{pet_breed}}", profile.breed)
            .replace("{{pet_age}}", profile.age)
            .replace("{{pet_weight}}", profile.weight)
            .replace("{{chart_html}}", chartHtml)
            .replace("{{history_rows}}", historyRowsHtml)
    }
}
