package com.github.feeling.src.commands.slash.utils

import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.config.Config
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class Ping : SlashCommandData(
    name = "ping",
    description = "Veja meu ping",
    run = {interaction ->
        HandlerPing.executeSlash(interaction)
    }
)

object HandlerPing {
    fun executeSlash(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        event.hook.editOriginal("Pong 🏓").queue { reply ->
            Thread.sleep(3000)

            val ping = event.jda.gatewayPing
            val networkEmoji = Config().getEmoji("network")
            val editMessage = "$networkEmoji **| Client Ping: ``$ping``ms**"

            reply.editMessage(editMessage).queue()
        }
    }
}