package com.github.feeling.src.systens

import io.github.cdimascio.dotenv.dotenv
import okhttp3.*
import java.util.concurrent.TimeUnit

class SquareManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiId = dotenv()["SQUAREAPP_ID"]
    private val token = dotenv()["SQUAREAPI_KEY"]
     fun getStatusResponse(): Response {
        val request = Request.Builder()
            .url("https://api.squarecloud.app/v2/apps/$apiId/status")
            .header("Authorization", token)
            .build()

        return client.newCall(request).execute()
    }

    fun getLogsResponse(): Response {
        val request = Request.Builder()
            .url("https://api.squarecloud.app/v2/apps/$apiId/logs")
            .header("Authorization", token)
            .build()

        return client.newCall(request).execute()
    }

}