package com.github.feeling.src.commands.slash.square

import com.github.feeling.src.config.Config
import com.github.feeling.src.systens.SquareManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.awt.Color
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Level
import java.util.logging.Logger


class SquareStatus {
    private val config = Config()
    fun status(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(false).queue()

        val square = SquareManager()
        val response = square.getStatusResponse()

        val network = config.getEmoji("network")
        val ramEmoji = config.getEmoji("ram")
        val review = config.getEmoji("review")
        val squareEmoji = config.getEmoji("square_cloud")
        val ssd = config.getEmoji("ssd")
        val cpuEmoji = config.getEmoji("cpu")

        if (response.isSuccessful) {
            val body = response.body.string()
            val json = JSONObject(body)

            if (json.getString("status") == "success") {
                val responseData = json.getJSONObject("response")
                val cpu = responseData.optString("cpu", "N/A")
                val ram = responseData.optString("ram", "N/A")
                val status = responseData.optString("status", "N/A")
                val storage = responseData.optString("storage", "N/A")
                val networkTotal = responseData.getJSONObject("network").optString("total", "N/A")
                val networkNow = responseData.getJSONObject("network").optString("now", "N/A")
                val uptime = responseData.optLong("uptime", -1)

                val uptimeDate = if (uptime != -1L) {
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(uptime), ZoneId.systemDefault())
                } else {
                    LocalDateTime.now()
                }

                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                val formattedUptime = uptimeDate.format(formatter)

                val embed = EmbedBuilder()
                    .setColor(Color.decode(Config().colorEmbed))
                    .setTitle("$squareEmoji SquareCloud App Status")
                    .setThumbnail("https://i.imgur.com/9Vzc9AQ.png")
                    .setDescription("""
                    - $cpuEmoji **CPU Usage:** ``$cpu``
                    - $ramEmoji **RAM Usage:** ``$ram``
                    - $review **Status:** ``$status``
                    - $ssd **Storage:** ``$storage``
                    - $network Network Total: ``$networkTotal``
                    - $network **Network Now:** ``$networkNow``
                    - 🕓 **Uptime (Date):** ``$formattedUptime``
                """.trimIndent())
                    .build()

                event.hook.editOriginalEmbeds(embed).queue()
            } else {
                event.hook.editOriginal("Failed to fetch status data.").queue()
            }
        } else {
            Logger.getLogger(OkHttpClient::class.java.name).setLevel(Level.FINE)
            event.hook.editOriginal("Failed to fetch status data.").queue()
        }
    }
}