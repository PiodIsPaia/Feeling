package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class Hug : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val prefix = getPrefix(event.guild) ?: Bot().prefix
        val contentRaw = event.message.contentRaw

        if (contentRaw.startsWith("${prefix}hug")) {
            val sender = event.author
            val receiver = event.message.mentions.members[0].user
            val powerEmoji = Bot().getEmoji("power_hug")

            val h = SendGif(event, "anime hug", "## $powerEmoji | ${sender.asMention} acabou de dar um abraço em ${receiver.asMention}")
            h.send()
        }
    }
}
