package com.github.feeling.src.systens

import okhttp3.*
import java.io.File
import java.security.MessageDigest


class VirusTotalManager(private val apiKey: String) {
    private val apiUrl = "https://www.virustotal.com/api/v3/"

    fun calculateHash(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val inputStream = file.inputStream()
        val byteArray = ByteArray(8192)
        var bytesRead: Int

        while (inputStream.read(byteArray).also { bytesRead = it } != -1) {
            digest.update(byteArray, 0, bytesRead)
        }
        val hashBytes = digest.digest()

        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun scanFile(file: File): String {
        val hash = calculateHash(file)
        val apiUrlFiles = "$apiUrl/files/$hash"
        val request = Request.Builder()
            .url(apiUrlFiles)
            .header("x-apikey", apiKey)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        return response.body.string()
    }

    fun getFileAnalysis(link: String): String {
        val request = Request.Builder()
            .url(link)
            .header("x-apikey", apiKey)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        return response.body.string() ?: ""
    }
}