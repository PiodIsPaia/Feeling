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

        if (event.message.contentRaw.startsWith("${prefix}hug")) {
            val args = event.message.contentRaw.split(" ")
            if (args.size == 2 && event.message.mentions.members.size == 1) {
                val sender = event.author
                val receiver = event.message.mentions.members[0].user

                val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

                if (prefixCommandsActive) {
                    val theme = "anime romance hug"
                    val tenorSearch = TenorSearch(theme)
                    val gifUrls = tenorSearch.searchGifs()

                    if (gifUrls.isNotEmpty()) {
                        val gifUrl = gifUrls.random()

                        val embed = EmbedBuilder()
                            .setImage(gifUrl.first)
                            .setFooter("Fonte: ${gifUrl.second}", null)
                            .setColor(Color.decode("#2b2d31"))
                            .build()

                        val powerEmoji = Bot().getEmoji("power_hug")

                        event.message.replyEmbeds(embed).addContent("## $powerEmoji | ${sender.asMention} acabou de dar um abraço em ${receiver.asMention}").queue()
                    } else {
                        println("Nenhum GIF encontrado para o tema '$theme'.")
                    }
                } else return
            } else {
                event.message.reply("Por favor, mencione uma pessoa para abraçar.").queue()
            }
        }
    }
}
