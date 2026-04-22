package com.proj.smart_feeder.feature_reports.presentation

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient

class ReportPrinter(private val context: Context) {
    fun print(htmlContent: String, reportName: String) {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter = view.createPrintDocumentAdapter(reportName)

                printManager.print(
                    reportName, printAdapter, PrintAttributes.Builder().build()
                )
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
    }
}