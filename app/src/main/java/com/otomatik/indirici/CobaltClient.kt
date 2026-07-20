package com.otomatik.indirici

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object CobaltClient {

    private val INSTANCES = listOf(
        "cobalt-video-indirici.onrender.com",
        "co.wuk.sh",
        "cobalt-api.hyper.lol",
        "cobalt.api.timelessnesses.me",
        "api-dl.cgm.rs",
        "capi.oak.li",
        "co.tskau.team"
    )

    private const val USER_AGENT = "VideoIndirici/1.0"

    // Hata ayıklama için son karşılaşılan hatayı burada tutuyoruz
    var lastError: String = ""
        private set

    fun resolveVideoUrl(pageUrl: String): String? {
        lastError = ""
        for (instance in INSTANCES) {
            val result = tryInstance(instance, pageUrl)
            if (result != null) return result
        }
        return null
    }

    private fun tryInstance(instance: String, pageUrl: String): String? {
        val apiUrl = "https://$instance/"
        var conn: HttpURLConnection? = null
        return try {
            conn = URL(apiUrl).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("User-Agent", USER_AGENT)
            conn.doOutput = true
            conn.connectTimeout = 20000
            conn.readTimeout = 30000

            val body = JSONObject().apply { put("url", pageUrl) }.toString()
            conn.outputStream.use { it.write(body.toByteArray()) }

            val code = conn.responseCode

            // Hata da olsa gövdeyi okumaya çalışıyoruz (errorStream)
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream?.bufferedReader()?.use { it.readText() } ?: ""

            if (code !in 200..299) {
                lastError = "[$instance] HTTP $code: ${responseText.take(200)}"
                return null
            }

            val json = JSONObject(responseText)

            when (json.optString("status")) {
                "tunnel", "redirect", "stream" -> {
                    val url = json.optString("url")
                    if (url.isNotBlank()) url else {
                        lastError = "[$instance] status var ama url boş: $responseText"
                        null
                    }
                }
                "picker" -> {
                    val picker = json.optJSONArray("picker")
                    if (picker != null && picker.length() > 0) {
                        picker.getJSONObject(0).optString("url").ifBlank { null }
                    } else {
                        lastError = "[$instance] picker boş: $responseText"
                        null
                    }
                }
                "error" -> {
                    val errorObj = json.optJSONObject("error")
                    val errorCode = errorObj?.optString("code") ?: "bilinmeyen"
                    lastError = "[$instance] Cobalt hatası: $errorCode"
                    null
                }
                else -> {
                    lastError = "[$instance] Beklenmeyen cevap: ${responseText.take(200)}"
                    null
                }
            }
        } catch (e: Exception) {
            lastError = "[$instance] İstisna: ${e.javaClass.simpleName} - ${e.message}"
            null
        } finally {
            conn?.disconnect()
        }
    }
}
