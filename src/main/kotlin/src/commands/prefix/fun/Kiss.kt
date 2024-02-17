package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.commands.prefix.PrefixCommands
import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class Kiss : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val prefix = getPrefix(event.guild) ?: Bot().prefix
        val content = event.message.contentRaw

        if (content.startsWith("${prefix}kiss")) {
            val sender = event.author
            val receiver = event.message.mentions.members[0].user
            val kiss = Bot().getEmoji("joikiss_gif")

            val k = SendGif(event, "anime kiss", "## $kiss | ${sender.asMention} acabou de dar uma beijoca em ${receiver.asMention}")
            k.send()
        }
    }
}