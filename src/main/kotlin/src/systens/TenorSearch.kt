package com.github.feeling.src.systens

import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class TenorSearch(private val theme: String) {
    private val client = OkHttpClient()

    fun searchGifs(): List<Pair<String, String>> {
        val dotenv = dotenv()
        val apiKey = dotenv["TENOR_KEY"]
        val clientKey = "Feeling"
        val limit = 100
        val encodedSearchTerm = URLEncoder.encode(theme, "UTF-8")
        val url = "https://tenor.googleapis.com/v2/search?q=$encodedSearchTerm&key=$apiKey&client_key=$clientKey&limit=$limit"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(response.body.string())
                return parseGifUrls(jsonResponse)
            } else {
                println("Erro ao fazer solicitação HTTP: ${response.code} ${response.message}")
            }
        }
        return emptyList()
    }

    private fun parseGifUrls(response: JSONObject): List<Pair<String, String>> {
        val gifUrlsWithDescriptions = mutableListOf<Pair<String, String>>()
        val results = response.getJSONArray("results")
        for (i in 0 until results.length()) {
            val result = results.getJSONObject(i)
            val contentDescription = result.optString("content_description")
            val mediaFormats = result.getJSONObject("media_formats")
            val gif = mediaFormats.optJSONObject("gif")
            gif?.let {
                val url = it.optString("url")
                gifUrlsWithDescriptions.add(Pair(url, contentDescription))
            }
        }
        return gifUrlsWithDescriptions
    }
}