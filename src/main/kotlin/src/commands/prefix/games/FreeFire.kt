package com.github.feeling.src.commands.prefix.games

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class FreeFire : ListenerAdapter() {
    companion object {
        val players = mutableListOf<String>()
        val minimumPlayers: Int = 2
        val playersLimit: Int = 8
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val content = event.message.contentRaw
        val prefix = getPrefix(event.guild) ?: Bot().prefix

        if (content.startsWith(prefix + "ff")) {
            val buttonJoin = Button.success("button_ff_join", "Entrar")
            val buttonPlayer = Button.secondary("button_ff_players_count", "Jogadores(min ${FreeFire.minimumPlayers}/${players.size})")
                .withDisabled(true)

            val loading = Bot().getEmoji("loading")

            val message = """
                ## $loading Aguando os jogadores do X1
                
                **Participantes:**
            """.trimIndent()

            val embed = EmbedBuilder()
                .setDescription(message)
                .setColor(Color.decode(Bot().colorEmbed))
                .build()

            event.message.replyEmbeds(embed).setActionRow(buttonJoin, buttonPlayer).queue()
        }
    }
}
