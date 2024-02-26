package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.commands.prefix.PrefixCommandBuilder
import com.github.feeling.src.config.Config
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.systens.TenorSearch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color

class Hello : PrefixCommandBuilder {
    override val name: String = "hello"
    override val aliases: Array<String> = arrayOf("hi", "hellow")
    override val action: (MessageReceivedEvent) -> Unit = {event ->
        execute(event)
    }

    fun execute(event: MessageReceivedEvent) {
        val sender = event.author
        val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

        if (prefixCommandsActive) {
            val theme = "anime greeting"
            val tenorSearch = TenorSearch(theme)
            val gifUrls = tenorSearch.searchGifs()

            if (gifUrls.isNotEmpty()) {
                val gifUrl = gifUrls.random()
                val embed = EmbedBuilder()
                    .setImage(gifUrl.first)
                    .setFooter("Fonte: ${gifUrl.second}", null)
                    .setColor(Color.decode("#2b2d31"))
                    .build()

                val greetingEmoji = Config().getEmoji("gura_greeting")

                event.message.replyEmbeds(embed)
                    .addContent("## $greetingEmoji | ${sender.asMention} está saudando a todos ").queue()
            } else {
                println("Nenhum GIF encontrado para o tema '$theme'.")
            }
        }
    }
}
