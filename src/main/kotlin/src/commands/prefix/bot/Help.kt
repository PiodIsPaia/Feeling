package com.github.feeling.src.commands.prefix.bot

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import java.awt.Color

class Help : ListenerAdapter() {
    private val config = Config()

    companion object {
        lateinit var embed: MessageEmbed
        lateinit var menu: SelectMenu
    }

    fun execute(event: MessageReceivedEvent) {
        val botName = event.jda.selfUser.name
        val message = getMessage(botName)
        menu = createHelpMenu()

        embed = EmbedBuilder()
            .setThumbnail("https://emoji.discadia.com/emojis/a07a40fb-d224-452e-b125-abc0cefbc8ea.PNG")
            .setDescription(message)
            .setColor(Color.decode(config.colorEmbed))
            .setFooter("Aqui estão as categorias de comandos disponíveis:", event.jda.selfUser.avatarUrl)
            .build()

        event.message.replyEmbeds(embed).setActionRow(menu).queue()
    }

    private fun getMessage(name: String?): String {
        return  """
            ## Olá! Eu sou a ``$name`` :wave: .
            ### Posso te ajudar com comandos de:
            
             - **Economia** 
             - **Moderação**
             - **Diversão**
             - **Outros**
        """.trimIndent()
    }

    private fun createHelpMenu(): SelectMenu {
        return StringSelectMenu.create("menu_help_command")
            .setPlaceholder("Selecione alguma opção ⭐.")
            .setMaxValues(1)
            .addOption("Economia", "menu_help_economy", "Veja meus comandos de economia", Emoji.fromUnicode("💰"))
            .addOption("Moderação", "menu_help_mod", "Veja meus comandos para a moderação do servidor", Emoji.fromUnicode("👮‍♂️"))
            .addOption("Diversão", "menu_help_fun", "Veja meus comandos para entretenimento do chat", Emoji.fromUnicode("🤡"))
            .addOption("Premium", "menu_help_premium", "Comandos para pessoas comm premium", Emoji.fromUnicode("💰"))
            .addOption("Módulos", "menu_help_modules", "Configure meus módulos", Emoji.fromFormatted("⚙"))
            .build()
    }
}
