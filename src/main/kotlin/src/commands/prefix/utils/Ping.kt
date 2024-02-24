package com.github.feeling.src.commands.prefix.utils

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Ping : ListenerAdapter() {
    fun execute(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)
        val response = if (prefixCommandsActive) "Pong 🏓" else return

        event.message.reply(response).queue { reply ->
            Thread.sleep(3000)

            val ping = event.jda.gatewayPing
            val networkEmoji = Config().getEmoji("network")
            val editMessage = "$networkEmoji **| Client Ping: ``$ping``ms**"

            reply.editMessage(editMessage).queue()
        }
    }
}