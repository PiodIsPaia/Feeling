package com.github.feeling.src.commands.prefix.utils

import com.github.feeling.src.config.Config
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Ping : ListenerAdapter() {
    fun execute(event: MessageReceivedEvent) {
        event.message.reply("Pong 🏓").queue { reply ->
            Thread.sleep(3000)

            val ping = event.jda.gatewayPing
            val networkEmoji = Config().getEmoji("network")
            val editMessage = "$networkEmoji **| Client Ping: ``$ping``ms**"

            reply.editMessage(editMessage).queue()
        }
    }
}