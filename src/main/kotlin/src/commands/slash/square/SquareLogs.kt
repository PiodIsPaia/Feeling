package com.github.feeling.src.commands.slash.square

import com.github.feeling.src.config.Config
import com.github.feeling.src.systens.SquareManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.json.JSONObject
import java.awt.Color

class SquareLogs {
    private val config = Config()
    fun logs(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(false).queue()

        val square = SquareManager()
        val response = square.getLogsResponse()

        if (response.isSuccessful) {
            val body = response.body.string()
            val json = JSONObject(body)

            if (json.getString("status") == "success") {
                val logs = json.getJSONObject("response").getString("logs")

                val squareEmoji = config.getEmoji("square_cloud")

                val embed = EmbedBuilder()
                    .setThumbnail("https://i.imgur.com/9Vzc9AQ.png")
                    .setColor(Color.decode(Config().colorEmbed))
                    .setTitle("$squareEmoji SquareCloud App Logs")
                    .setDescription("```\n$logs\n```")
                    .build()

                event.hook.editOriginalEmbeds(embed).queue()
            } else {
                event.hook.editOriginal("Failed to fetch logs.").queue()
            }
        } else {
            event.hook.editOriginal("Failed to fetch logs.").queue()
        }
    }
}