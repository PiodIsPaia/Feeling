package com.github.feeling.src.components.freefire

import com.github.feeling.src.commands.prefix.games.FreeFire
import com.github.feeling.src.config.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color
import kotlin.random.Random

class FFComponents : ListenerAdapter() {
    private val words: List<String> = listOf(
        "errou o hud e tomou 3 capas pro",
        "deu um tapa de desert em",
        "morreu atras do gelo pro",
        "deu 3 capas em"
    )
    private var roundNumber = 0
    private val results = mutableListOf<String>()

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.user.isBot) return

        if (event.componentId == "button_ff_join") {
            val playerName = event.user.name
            if (!FreeFire.players.contains(playerName)) {
                FreeFire.players.add(playerName)
                updateEmbed(event)
            } else {
                event.reply("Você já está no jogo.").setEphemeral(true).queue()
            }
        }

        if (event.componentId == "button_ff_play") {
            startGame(event)
        }
    }

    private fun updateEmbed(event: ButtonInteractionEvent) {
        val buttonJoin = Button.success("button_ff_join", "Entrar")
        val buttonPlayers = Button.secondary("button_ff_players_count", "Jogadores (min 6/${FreeFire.players.size})").withDisabled(true)
        var buttonPlay = Button.success("button_ff_play", "Começar")

        buttonPlay = if (FreeFire.players.size < FreeFire.minimum) {
            buttonPlay.withDisabled(true)
        } else {
            buttonPlay.withDisabled(false)
        }

        val formattedPlayers = FreeFire.players.mapIndexed { index, player -> "${index + 1}. ``$player``" }.joinToString("\n")
        val loading = Bot().getEmoji("loading")

        val title = if (FreeFire.players.size < 6) "$loading Aguardando os jogadores do X1" else "Rodada $roundNumber"

        val embedBuilder = EmbedBuilder()
            .setColor(Color.decode("#2b2d31"))
            .setDescription("## $title\n**Participantes:**\n$formattedPlayers")

        val embed = embedBuilder.build()

        event.editMessageEmbeds(embed).setActionRow(buttonPlay, buttonJoin, buttonPlayers).queue()
    }

    private fun startGame(event: ButtonInteractionEvent) {
        if (FreeFire.players.size < FreeFire.minimum) {
            event.reply("O jogo requer no mínimo dois jogadores para começar.").setEphemeral(true).queue()
            return
        }

        roundNumber++
        updateEmbed(event)

        val shuffledPlayers = FreeFire.players.shuffled()
        val pairs = shuffledPlayers.chunked(2)

        pairs.forEachIndexed { index, pair ->
            val player1 = pair[0]
            val player2 = pair[1]
            val result = simulateMatch(player1, player2)
            results.add("**Partida ${index + 1}:** - **Resultado:** $result")
        }

        showRoundResults(event)
    }

    private fun showRoundResults(event: ButtonInteractionEvent) {
        val embedBuilder = EmbedBuilder()
            .setColor(Color.decode("#2b2d31"))
            .setTitle("Resultados da Rodada $roundNumber")

        results.forEach { result ->
            embedBuilder.addField("\u200B", result, false)
        }

        val embed = embedBuilder.build()
        event.hook.editOriginalEmbeds(embed).queue {
            results.clear()
            checkWinner(event)
        }
    }

    private fun checkWinner(event: ButtonInteractionEvent) {
        if (FreeFire.players.size == 1) {
            val winner = FreeFire.players.first()
            val embed = EmbedBuilder()
                .setColor(Color.decode(Bot().colorEmbed))
                .setTitle("🏆 O Vencedor do X1")
                .setDescription("O vencedor do torneio é: ``$winner``")
                .build()

            event.channel.sendMessageEmbeds(embed).queue {
                FreeFire.players.clear()
                roundNumber = 0
            }
        }
    }

    private fun simulateMatch(player1: String, player2: String): String {
        val winner = if (Random.nextBoolean()) player1 else player2
        val loser = if (winner == player1) player2 else player1
        val word = words.random()
        FreeFire.players.remove(loser)

        return "``$winner`` $word ``$loser``"
    }
}
