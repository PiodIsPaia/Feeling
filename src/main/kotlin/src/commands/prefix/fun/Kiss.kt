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
            val args = content.split(" ")
            if (args.size == 2 && event.message.mentions.members.size == 1) {
                val sender = event.author
                val receiver = event.message.mentions.members[0].user

                val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

                if (prefixCommandsActive) {
                    val theme = "anime romance kiss"
                    val tenorSearch = TenorSearch(theme)
                    val gifUrls = tenorSearch.searchGifs()

                    if (gifUrls.isNotEmpty()) {
                        val gifUrl = gifUrls.random()

                        val embed = EmbedBuilder()
                            .setImage(gifUrl.first)
                            .setFooter("Fonte: ${gifUrl.second}", null)
                            .setColor(Color.decode("#2b2d31"))
                            .build()

                        val kiss = Bot().getEmoji("joikiss_gif")

                        event.message.replyEmbeds(embed).addContent("## $kiss | ${sender.asMention} acabou de dar uma beijoca em ${receiver.asMention}").queue()
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