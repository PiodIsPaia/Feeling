package com.github.feeling.src.commands.prefix.`fun`

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class Hello : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val prefix = getPrefix(event.guild) ?: Bot().prefix
        val contentRaw = event.message.contentRaw.lowercase()

        if (contentRaw.startsWith(prefix)) {
            val command = contentRaw.substring(prefix.length).trim()

            if (isGreetingCommand(command)) {
                GreetingResponder(event).respondToGreeting()
            }
        }
    }

    private fun isGreetingCommand(command: String): Boolean {
        val greetings = arrayOf("hi", "hello", "hl")
        return greetings.any { command.startsWith(it) }
    }

    private class GreetingResponder(private val event: MessageReceivedEvent) {
        fun respondToGreeting() {
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

                    val greetingEmoji = Bot().getEmoji("gura_greeting")

                    event.message.replyEmbeds(embed)
                        .addContent("## $greetingEmoji | ${sender.asMention} está saudando a todos ").queue()
                } else {
                    println("Nenhum GIF encontrado para o tema '$theme'.")
                }
            }
        }
    }
}
