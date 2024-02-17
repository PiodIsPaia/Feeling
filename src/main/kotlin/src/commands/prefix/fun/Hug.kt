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
            val mentions = event.message.mentions.members
            val powerEmoji = Bot().getEmoji("power_hug")

            val message = if (mentions.isNotEmpty()) {
                val receiver = mentions[0].user
                "## $powerEmoji ${sender.asMention} acabou de dar um abraço em ${receiver.asMention}"
            } else {
                "## $powerEmoji ${sender.asMention} deu um abraço em mim ✨"
            }

            val h = SendGif(event, "anime hug", message)
            h.send()
        }
    }
}
