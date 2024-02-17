package com.github.feeling.src.commands.prefix.utils

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class Avatar : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val msg = event.message.contentRaw
        val prefix = getPrefix(event.guild) ?: Bot().prefix

        if (msg.startsWith("${prefix}avatar")) {
            val mentionedUserId = event.message.mentions.users.firstOrNull()?.id
            val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

            if (prefixCommandsActive) {
                if (mentionedUserId != null) {
                    event.guild.retrieveMemberById(mentionedUserId).queue { mentionedMember ->
                        val embed = EmbedBuilder()
                            .setAuthor(event.author.name, null, event.author.avatarUrl)
                            .setTitle("📸 **| Avatar de ``${mentionedMember.user.name}``**")
                            .setImage(mentionedMember.user.avatarUrl + "?size=1024")
                            .setColor(Color.decode("#2b2d31"))
                            .build()

                        val button = Button.link(mentionedMember.user.avatarUrl.toString() + "?size=1024", "Abrir no navegador")

                        event.message.replyEmbeds(embed).setActionRow(button).queue()
                    }
                } else {
                    event.message.reply("Você precisa mencionar um membro válido após o comando `${prefix}avatar`.").queue()
                }
            } else {
                return
            }
        }
    }
}
