package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.getPrefix
import com.github.feeling.src.systens.SendGif
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Kiss : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val prefix = getPrefix(event.guild) ?: Bot().prefix
        val content = event.message.contentRaw

        if (content.startsWith("${prefix}kiss")) {
            val sender = event.author
            val mentions = event.message.mentions.members
            val kiss = Bot().getEmoji("joikiss_gif")

            val message = if (mentions.isNotEmpty()) {
                val receiver = mentions[0].user
                "## $kiss | ${sender.asMention} acabou de dar uma beijoca em ${receiver.asMention}"
            } else {
                "## $kiss | ${sender.asMention} deu um beijo em mimm!!"
            }

            val k = SendGif(event, "anime kiss", message)
            k.send()
        }
    }
}
