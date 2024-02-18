package com.github.feeling.src.components.freefire

import com.github.feeling.src.commands.prefix.games.FreeFire
import com.github.feeling.src.config.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
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
    private val playersStats = mutableMapOf<String, Pair<Int, Int>>()
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var isTournamentRunning = false
    private lateinit var message: InteractionHook

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
        val buttonPlayers =
            Button.secondary("button_ff_players_count", "Jogadores (min ${FreeFire.minimum}/${FreeFire.players.size})").withDisabled(true)
        var buttonPlay = Button.success("button_ff_play", "Começar")

        buttonPlay = if (FreeFire.players.size < FreeFire.minimum) {
            buttonPlay.withDisabled(true)
        } else {
            buttonPlay.withDisabled(false)
        }

        val formattedPlayers =
            FreeFire.players.mapIndexed { index, player -> "${index + 1}. ``$player``" }.joinToString("\n")
        val loading = Bot().getEmoji("loading")

        val title = if (FreeFire.players.size < 6) "$loading Aguardando os jogadores do X1" else "Rodada $roundNumber"

        val embedBuilder = EmbedBuilder()
            .setColor(Color.decode("#2b2d31"))
            .setDescription("## $title\n**Participantes:**\n$formattedPlayers")

        val embed = embedBuilder.build()

        message = event.editMessageEmbeds(embed).setActionRow(buttonPlay, buttonJoin, buttonPlayers).complete()

    }

    private fun startGame(event: ButtonInteractionEvent) {
        if (FreeFire.players.size < FreeFire.minimum) {
            event.reply("O jogo requer no mínimo dois jogadores para começar.").setEphemeral(true).queue()
            return
        }

        isTournamentRunning = true
        roundNumber++
        updateEmbed(event)

        val shuffledPlayers = FreeFire.players.shuffled()
        val pairs = shuffledPlayers.chunked(2)

        pairs.forEachIndexed { index, pair ->
            val player1 = pair[0]
            val player2 = pair[1]
            val result = simulateMatch(player1, player2)
            // Agendar a exibição do resultado com atraso de 2 segundos
            scheduler.schedule({
                results.add("**Partida ${index + 1}:** - **Resultado:** $result")
                showRoundResults(event)
            }, 2 * index.toLong(), TimeUnit.SECONDS)
        }
    }

    private fun scheduleNextRound(event: ButtonInteractionEvent) {
        if (FreeFire.players.size >= 2) {
            // Agendar a próxima rodada após 2 segundos
            scheduler.schedule({
                startGame(event)
            }, 2, TimeUnit.SECONDS)
        } else if (FreeFire.players.size == 1) {
            checkWinner(event)
        } else {
            // Se não houver jogadores suficientes, encerrar o torneio
            event.reply("Não há jogadores suficientes para continuar o torneio.").setEphemeral(true).queue()
        }
    }

    private fun showRoundResults(event: ButtonInteractionEvent) {
        val embedBuilder = EmbedBuilder()
            .setColor(Color.decode("#2b2d31"))
            .setTitle("Resultados da Rodada $roundNumber")

        results.forEach { result ->
            embedBuilder.addField("\u200B", result, false)
        }

        val embed = embedBuilder.build()
        message.editOriginalEmbeds(embed).queue {
            message.editOriginalComponents().queue()
            // Limpar resultados para a próxima rodada
            results.clear()
            // Verificar se existe um vencedor
            if (!isTournamentRunning) {
                showGameStats(event)
            } else {
                scheduleNextRound(event)
            }
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
                isTournamentRunning = false
                showGameStats(event)
            }
        }
    }

    private fun simulateMatch(player1: String, player2: String): String {
        val winner = if (Random.nextBoolean()) player1 else player2
        val loser = if (winner == player1) player2 else player1
        val word = words.random()
        FreeFire.players.remove(loser)

        playersStats[winner] = playersStats.getOrDefault(winner, Pair(0, 0)).let { (wins, kills) -> Pair(wins + 1, kills) }
        playersStats[loser] = playersStats.getOrDefault(loser, Pair(0, 0)).let { (wins, kills) -> Pair(wins, kills + 1) }

        return "``$winner`` $word ``$loser``"
    }

    private fun showGameStats(event: ButtonInteractionEvent) {
        val embedBuilder = EmbedBuilder()
            .setColor(Color.decode("#2b2d31"))
            .setTitle("Estatísticas da Partida")

        playersStats.forEach { (player, stats) ->
            val (wins, kills) = stats
            embedBuilder.addField("``$player``", "Vitórias: $wins, Mortes: $kills", false)
        }

        val embed = embedBuilder.build()
        event.channel.sendMessageEmbeds(embed).queue()
    }
}


