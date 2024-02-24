package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.utils.getPrefix
import com.github.feeling.src.systens.SendGif
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Hug : ListenerAdapter() {
    fun execute(event: MessageReceivedEvent) {

        val sender = event.author
        val mentions = event.message.mentions.members
        val powerEmoji = Config().getEmoji("power_hug")

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
