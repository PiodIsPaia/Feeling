package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.database.utils.arePrefixCommandsActive
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color

class SendGif(private val event: MessageReceivedEvent, private val theme: String, private val message: String) {
    fun send() {
        val args = event.message.contentRaw.split(" ")
        if (args.size == 2 && event.message.mentions.members.size == 1) {
            val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

            if (prefixCommandsActive) {

                val tenorSearch = TenorSearch(theme)
                val gifUrls = tenorSearch.searchGifs()

                if (gifUrls.isNotEmpty()) {
                    val gifUrl = gifUrls.random()
                    val embed = EmbedBuilder()
                        .setImage(gifUrl.first)
                        .setFooter("Fonte: ${gifUrl.second}", null)
                        .setColor(Color.decode("#2b2d31"))
                        .build()

                    event.message.replyEmbeds(embed)
                        .addContent(message)
                        .queue()
                } else {
                    println("Nenhum GIF encontrado para o tema '$theme'.")
                }
            } else return
        } else {
            event.message.reply("Por favor, mencione uma pessoa para abraçar.").queue()
        }
    }
}