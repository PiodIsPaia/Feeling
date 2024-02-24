package com.github.feeling.src.commands.prefix.games

import com.github.feeling.src.config.Config
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

    fun execute(event: MessageReceivedEvent) {

        val buttonJoin = Button.success("button_ff_join", "Entrar")
        val buttonPlayer = Button.secondary("button_ff_players_count", "Jogadores(min ${FreeFire.minimumPlayers}/${players.size})")
            .withDisabled(true)

        val loading = Config().getEmoji("loading")

        val message = """
                ## $loading Aguando os jogadores do X1
                
                **Participantes:**
            """.trimIndent()

        val embed = EmbedBuilder()
            .setDescription(message)
            .setColor(Color.decode(Config().colorEmbed))
            .build()

        event.message.replyEmbeds(embed).setActionRow(buttonJoin, buttonPlayer).queue()

    }
}
