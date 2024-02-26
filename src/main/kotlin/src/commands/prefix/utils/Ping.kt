package com.github.feeling.src.commands.prefix.utils

import com.github.feeling.src.commands.prefix.PrefixCommandBuilder
import com.github.feeling.src.config.Config
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Ping : PrefixCommandBuilder {
    override val name: String = "ping"
    override val aliases: Array<String> = arrayOf("latência", "latency")
    override val action: (MessageReceivedEvent) -> Unit = {event ->
        execute(event)
    }

    private fun execute(event: MessageReceivedEvent) {
        event.message.reply("Pong 🏓").queue { reply ->
            Thread.sleep(3000)

            val ping = event.jda.gatewayPing
            val networkEmoji = Config().getEmoji("network")
            val editMessage = "$networkEmoji **| Client Ping: ``$ping``ms**"

            reply.editMessage(editMessage).queue()
        }
    }
}