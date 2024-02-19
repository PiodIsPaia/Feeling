package com.github.feeling.src.commands.prefix.bot

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import java.awt.Color

class Help : ListenerAdapter() {
    private val bot = Bot()

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val content = event.message.contentRaw
        val prefix = getPrefix(event.guild) ?: bot.prefix

        if (content.startsWith(prefix + "ajuda") || content.startsWith(prefix + "help")) {
            val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

            if (prefixCommandsActive) {
                handleHelpCommand(event)
            } else {
                return
            }
        }
    }

    private fun handleHelpCommand(event: MessageReceivedEvent) {
        val botName = event.jda.selfUser.name
        val message = getMessage(botName)
        val menu = createHelpMenu()

        val embed = EmbedBuilder()
            .setDescription(message)
            .setColor(Color.decode(bot.colorEmbed))
            .build()

        event.message.replyEmbeds(embed).setActionRow(menu).queue()
    }

    private fun getMessage(name: String?): String {
        return  """
            Olá! Eu sou a ``$name``. Posso te ajudar com comandos de economia, moderação e diversão.
            Aqui estão as categorias de comandos disponíveis:
        """.trimIndent()
    }

    private fun createHelpMenu(): SelectMenu {
        return StringSelectMenu.create("menu_help_command")
            .addOption("Economia", "e", "Veja meus comandos de economia", Emoji.fromUnicode("💰"))
            .addOption("Moderação", "m", "Veja meus comandos para a moderação do servidor", Emoji.fromUnicode("👮‍♂️"))
            .addOption("Diversão", "d", "Veja meus comandos para entretenimento do chat", Emoji.fromUnicode("🤡"))
            .build()
    }
}
