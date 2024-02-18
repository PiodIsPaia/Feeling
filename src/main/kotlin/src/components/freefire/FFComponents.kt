package com.github.feeling.src.components.freefire

import com.github.feeling.src.commands.prefix.games.FreeFire
import com.github.feeling.src.config.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color
import java.util.*
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

        when (event.componentId) {
            "button_ff_join" -> handleJoinButton(event)
            "button_ff_play" -> startGame(event)
            "button_ff_exit_game" -> handleExitButton(event)
        }
    }

    private fun handleJoinButton(event: ButtonInteractionEvent) {
        val playerName = event.user.name
        if (!FreeFire.players.contains(playerName)) {
            FreeFire.players.add(playerName)
            updateEmbed(event)
        } else {
            event.reply("Você já está no jogo.").setEphemeral(true).queue()
        }
    }

    private fun handleExitButton(event: ButtonInteractionEvent) {
        val playerName = event.user.name
        if (FreeFire.players.contains(playerName)) {
            FreeFire.players.remove(playerName)
            updateEmbed(event)
        }
    }

    private fun updateEmbed(event: ButtonInteractionEvent) {
        val buttonJoin = if (FreeFire.players.size < FreeFire.playersLimit) {
            Button.success("button_ff_join", "Entrar")
        } else {
            Button.danger("button_ff_join", "Limite atingindo").withDisabled(true)
        }

        val buttonPlayers =
            Button.secondary("button_ff_players_count", "Jogadores (min ${FreeFire.minimumPlayers}/${FreeFire.players.size})").withDisabled(true)
        var buttonPlay = Button.success("button_ff_play", "Começar")

        buttonPlay = if (FreeFire.players.size < FreeFire.minimumPlayers) {
            buttonPlay.withDisabled(true)
        } else {
            buttonPlay.withDisabled(false)
        }

        val buttonExit = Button.danger("button_ff_exit_game", "Sair")

        val formattedPlayers =
            FreeFire.players.mapIndexed { index, player -> "${index + 1}. ``$player``" }.joinToString("\n")
        val loading = Bot().getEmoji("loading")

        val title = if (FreeFire.players.size < FreeFire.minimumPlayers) "$loading Aguardando os jogadores" else "A partida pode ser iniciada"

        val embedBuilder = EmbedBuilder()
            .setColor(Color.decode("#2b2d31"))
            .setDescription("## $title\n**Participantes:**\n$formattedPlayers")

        val embed = embedBuilder.build()

        message = event.editMessageEmbeds(embed).setActionRow(buttonPlay, buttonJoin, buttonExit, buttonPlayers).complete()
    }

    private fun startGame(event: ButtonInteractionEvent) {
        if (FreeFire.players.size < FreeFire.minimumPlayers) {
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
            scheduler.schedule({
                results.add("**Partida ${index + 1}:** - **Resultado:** $result")
                showRoundResults(event)
            }, 2 * index.toLong(), TimeUnit.SECONDS)
        }
    }

    private fun scheduleNextRound(event: ButtonInteractionEvent) {
        if (FreeFire.players.size >= 2) {
            scheduler.schedule({
                startGame(event)
            }, 2, TimeUnit.SECONDS)
        } else if (FreeFire.players.size == 1) {
            checkWinner(event)
        } else {
            event.reply("Não há jogadores suficientes para continuar o torneio.").setEphemeral(true).queue()
        }
    }

    private fun showRoundResults(event: ButtonInteractionEvent) {
        val embedBuilder = EmbedBuilder()
            .setColor(Color.decode(Bot().colorEmbed))
            .setTitle("Resultados da Rodada $roundNumber")

        results.forEach { result ->
            embedBuilder.addField("\u200B", result, false)
        }

        val embed = embedBuilder.build()
        message.editOriginalEmbeds(embed).queue {
            message.editOriginalComponents().queue()
            results.clear()
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
            .setColor(Color.decode(Bot().colorEmbed))
            .setTitle("Estatísticas da Partida")

        playersStats.forEach { (player, stats) ->
            val (wins, kills) = stats
            embedBuilder.addField("**${player.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}**", "Vitórias: $wins\nMortes: $kills", true)
        }

        val embed = embedBuilder.build()
        event.channel.sendMessageEmbeds(embed).queue()
    }
}



