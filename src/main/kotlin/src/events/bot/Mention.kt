package com.github.feeling.src.events.bot

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class Mention : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val botMention = event.jda.selfUser.asMention
        val prefix = getPrefix(event.guild) ?: Bot().prefix

        if (isBotMentionedAlone(event.message.contentRaw, botMention)) {
            val botMentionResponder = BotMentionResponder(event, prefix)
            botMentionResponder.respondToMention()
        }
    }

    private fun isBotMentionedAlone(content: String, botMention: String): Boolean {
        return content == botMention
    }

    private class BotMentionResponder(private val event: MessageReceivedEvent, private val prefix: String) {

        fun respondToMention() {
            val greetingEmoji = Bot().getEmoji("gura_greeting")
            val botName = event.jda.selfUser.name

            val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)
            val response = if (prefixCommandsActive) "**${prefix}ajuda ** ou **/ajuda**" else "**/ajuda**"

            val message = """
                ## $greetingEmoji Olá! Eu sou a ``$botName``.
                
                > Meu prefixo neste servidor é: **$prefix**
                > Para ver todos os comandos disponíveis, digite: $response
            """.trimIndent()

            val embed = EmbedBuilder()
                .setAuthor(event.jda.selfUser.name, null, event.jda.selfUser.avatarUrl ?: event.jda.selfUser.defaultAvatarUrl)
                .setDescription(message)
                .setColor(Color.decode(Bot().colorEmbed))
                .build()

            event.message.replyEmbeds(embed).queue {
                Thread.sleep(30000)
                it.delete().queue()
            }
        }
    }
}

