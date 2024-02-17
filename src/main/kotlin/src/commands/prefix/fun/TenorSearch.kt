package com.github.feeling.src.commands.prefix.`fun`

import io.github.cdimascio.dotenv.dotenv
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder

class TenorSearch(private val theme: String) {
    fun searchGifs(): List<Pair<String, String>> {
        val dotenv = dotenv()
        val apiKey = dotenv["TENOR_KEY"]
        val clientKey = "Feeling"
        val limit = 100
        val encodedSearchTerm = URLEncoder.encode(theme, "UTF-8")
        val url = "https://tenor.googleapis.com/v2/search?q=$encodedSearchTerm&key=$apiKey&client_key=$clientKey&limit=$limit"

        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = StringBuilder()
            val reader = InputStreamReader(connection.inputStream)
            var char: Int
            while (reader.read().also { char = it } != -1) {
                response.append(char.toChar())
            }
            reader.close()

            //println("Resposta da API: $response")

            val jsonResponse = JSONObject(response.toString())
            return parseGifUrls(jsonResponse)
        } else {
            println("Erro ao fazer solicitação HTTP: ${connection.responseMessage}")
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

