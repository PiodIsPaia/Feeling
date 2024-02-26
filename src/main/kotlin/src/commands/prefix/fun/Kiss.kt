package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.commands.prefix.PrefixCommandBuilder
import com.github.feeling.src.config.Config
import com.github.feeling.src.systens.SendGif
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Kiss : PrefixCommandBuilder {
    override val name: String = "kiss"
    override val aliases: Array<String> = arrayOf()
    override val action: (MessageReceivedEvent) -> Unit = {event ->
        execute(event)
    }
    private fun execute(event: MessageReceivedEvent) {
        val sender = event.author
        val mentions = event.message.mentions.members
        val kiss = Config().getEmoji("joikiss_gif")

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
