package com.github.feeling.src.commands.prefix.squarecloud

import com.github.feeling.src.config.Config
import com.github.feeling.src.systens.SquareManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.awt.Color
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Level
import java.util.logging.Logger


class Status : ListenerAdapter() {
    private val client = OkHttpClient()
    private val b = Config()

    fun execute(event: MessageReceivedEvent) {

        val squareManager = SquareManager()
        val response = squareManager.getStatusResponse()

        val network = b.getEmoji("network")
        val ramEmoji = b.getEmoji("ram")
        val review = b.getEmoji("review")
        val square = b.getEmoji("square_cloud")
        val ssd = b.getEmoji("ssd")
        val cpuEmoji = b.getEmoji("cpu")

        if (response.isSuccessful) {
            val body = response.body.string()
            val json = JSONObject(body)

            if (json.getString("status") == "success") {
                val responseData = json.getJSONObject("response")
                val cpu = responseData.getString("cpu")
                val ram = responseData.getString("ram")
                val status = responseData.getString("status")
                val storage = responseData.getString("storage")
                val networkTotal = responseData.getJSONObject("network").getString("total")
                val networkNow = responseData.getJSONObject("network").getString("now")
                val uptime = responseData.getLong("uptime")

                val uptimeDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(uptime), ZoneId.systemDefault())

                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                val formattedUptime = uptimeDate.format(formatter)

                val embed = EmbedBuilder()
                    .setColor(Color.decode(Config().colorEmbed))
                    .setTitle("$square SquareCloud App Status")
                    .setThumbnail("https://media.discordapp.net/attachments/1149134050880127119/1210618474733371393/logo.png?ex=65eb3750&is=65d8c250&hm=72b3ff60f8379fe19631c0dba2314e1c78f694261f286190728af7973134680a&=&format=webp&quality=lossless")
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

                event.channel.sendMessageEmbeds(embed).queue()
            } else {
                event.channel.sendMessage("Failed to fetch status data.").queue()
            }
        } else {
            Logger.getLogger(OkHttpClient::class.java.name).setLevel(Level.FINE)
            event.channel.sendMessage("Failed to fetch status data.").queue()
        }
    }

}