package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.commands.prefix.PrefixCommandBuilder
import com.github.feeling.src.config.Config
import com.github.feeling.src.systens.SendGif
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Hug : PrefixCommandBuilder {
    override val name: String = "hug"
    override val aliases: Array<String> = arrayOf()
    override val action: (MessageReceivedEvent) -> Unit = {event ->
        execute(event)
    }

    private fun execute(event: MessageReceivedEvent) {
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
