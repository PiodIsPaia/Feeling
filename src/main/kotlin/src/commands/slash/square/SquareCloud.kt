package com.github.feeling.src.commands.slash.square

import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.commands.slash.SubCommand
import com.github.feeling.src.config.Config
import com.github.feeling.src.systens.SquareManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.json.JSONObject
import java.awt.Color
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SquareCloud : SlashCommandData(
    name = "feeling",
    description = "[ Feeling ] Veja meinhas informações.",
    subcommands = listOf(
        SubCommand(
            name = "status",
            description = "[ Host ] Veja meus status."
        ),
        SubCommand(
            name = "logs",
            description = "[ Host ] Veja meus ultimos logs."
        )
    ),
    run = { event ->
        when (event.subcommandName) {
            "status" -> SquareStatus().status(event)
            "logs" -> SquareLogs().logs(event)
        }
    }
)

