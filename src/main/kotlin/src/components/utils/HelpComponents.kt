package com.github.feeling.src.components.utils

import com.github.feeling.src.commands.prefix.bot.Help
import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.bson.Document
import java.awt.Color

class HelpComponents : ListenerAdapter() {
    private val config = Config()
    private val db = Database.instance
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (event.selectMenu.id == "menu_help_command") {
            val selectedValues = event.values

            val prefix = event.guild?.let { getPrefix(it) } ?: config.prefix

            for (option in selectedValues) {
                when (option) {
                    "menu_help_economy" -> handlerOptionEconomy(event, prefix)
                    "menu_help_mod" -> handlerOptionMod(event, prefix)
                    "menu_help_fun" -> handlerOptionFun(event, prefix)
                    "menu_help_premium" -> handlerOptionPremium(event, prefix)
                    "menu_help_modules" -> handlerOptionModules(event)
                }
            }
        }
    }
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId == "button_back_help_menu") {
            val embed = Help.embed
            val menu = Help.menu

            event.editMessageEmbeds(embed).setActionRow(menu).queue()
        }
    }

    private fun handlerOptionEconomy(event: StringSelectInteractionEvent, prefix: String) {
        val prefixCommandsActive = arePrefixCommandsActive(event.guild!!.id)

        val descriptionPrefix = if (prefixCommandsActive) buildString {
            appendLine("## ($prefix)Comandos por prefixo:")
            appendLine("- `${prefix}daily` (Resgate de forma random uma certa quantidade de **milhos diariamente** usando este comando 🌽)")
            appendLine("- `${prefix}saldo` (Veja sua carteira 💳)")
        } else ""

        val descriptionSlash = buildString {
            appendLine("## (/)Slash Commands:")
            appendLine("- Em breve...")
        }

        val message = """
            ## 💳 Economia 
            $descriptionPrefix
            
            $descriptionSlash
        """.trimIndent()

        val embed = EmbedBuilder()
            .setThumbnail("https://emoji.discadia.com/emojis/307246a2-804c-4024-b8ef-18eb77791e63.PNG")
            .setDescription(message)
            .setColor(Color.WHITE)
            .setFooter("Acima você poderar ver todos meus comandos de economia", event.jda.selfUser.avatarUrl)
            .build()

        val buttonBack = Button.secondary("button_back_help_menu", "Voltar")

        event.editMessageEmbeds(embed).setActionRow(buttonBack).queue()
    }

    private fun handlerOptionMod(event: StringSelectInteractionEvent, prefix: String) {
        val prefixCommandsActive = arePrefixCommandsActive(event.guild!!.id)

        val descriptionPrefix = if (prefixCommandsActive) buildString {
            appendLine("## ($prefix)Comandos por prefixo:")
            appendLine("- Em breve...")
        } else ""

        val descriptionSlash = buildString {
            appendLine("## (/)Slash Commands")
            appendLine("- ``/ban`` (Serve para banir algum encrenqueiro de seu servidor)")
            appendLine("- ``/limpar`` (Apague mensagens com menos de 7 dias de envio)")
            appendLine("- ``/tag criar`` (Crie uma **Tag** para agilizar na resposta de perguntas muitos frequentes no seu servidor)")
            appendLine("- ``/tag ver`` (Vizualize as **Tags** existente no servidor)")
            appendLine("- ``/tag excluir`` (Exclua uma tag a partir de seu nome)")
        }

        val message = buildString {
            appendLine("## 👮‍♂️ Moderação")
            appendLine(descriptionPrefix)
            appendLine(descriptionSlash)
        }

        val embed = EmbedBuilder()
            .setThumbnail("https://emoji.discadia.com/emojis/f95b6d2e-78fb-451d-8174-ed737562d48d.PNG")
            .setDescription(message)
            .setColor(Color.WHITE)
            .setFooter("Acima você poderar ver todos meus comandos de moderação", event.jda.selfUser.avatarUrl)
            .build()

        val buttonBack = Button.secondary("button_back_help_menu", "Voltar")

        event.editMessageEmbeds(embed).setActionRow(buttonBack).queue()
    }

    private fun handlerOptionFun(event: StringSelectInteractionEvent, prefix: String) {
        val prefixCommandsActive = arePrefixCommandsActive(event.guild!!.id)

        val descriptionPrefix = if (prefixCommandsActive) buildString {
            appendLine("## ($prefix)Comandos por prefixo:")
            appendLine("- ``${prefix}kiss @usuario`` (Beije algum apenas o mencionando após o comando)")
            appendLine("- ``${prefix}hug @usuario`` (Abrace alguém o mencionando após o comando)")
            appendLine("- ``${prefix}hi`` (Demonstre a todos do chat que você chegou mandando uma saudação)")
        } else return

        val descriptionSlash = buildString {
            appendLine("## (/)Slash commands")
            appendLine("- Em breve...")
        }

        val message = buildString {
            appendLine("## 🤡 Diversão")
            appendLine(descriptionPrefix)
            appendLine(descriptionSlash)
        }

        val embed = EmbedBuilder()
            .setThumbnail("https://emoji.discadia.com/emojis/f3d18f4a-34a6-4210-ac4c-bd72d48e96de.PNG")
            .setDescription(message)
            .setColor(Color.WHITE)
            .setFooter("Acima você poderar ver todos meus comandos de diversão", event.jda.selfUser.avatarUrl)
            .build()

        val buttonBack = Button.secondary("button_back_help_menu", "Voltar")

        event.editMessageEmbeds(embed).setActionRow(buttonBack).queue()
    }

    private fun handlerOptionPremium(event: StringSelectInteractionEvent, prefix: String) {
        val prefixCommandsActive = arePrefixCommandsActive(event.guild!!.id)

        val ia = if (isActive(event.guild!!.id)) "- ``@${event.jda.selfUser.name} bom dia`` (Você terá acesso a uma IA apenas me mencionando e logo após a menção escrevendo sua dúvida ou so conversar comigo mesmo)" else ""

        val descriptionPrefix = if (prefixCommandsActive) buildString {
            appendLine("## ($prefix)Comandos por prefixo:")
            appendLine("- ``${prefix}vr + algum arquivo`` (Este comando faz uso a API do VirusTotal para verificação de arquivos maliciosos) ")
            appendLine(ia)
        } else return

        val descriptionSlash = buildString {
            appendLine("## (/)SLash Commands")
            appendLine("- Em breve...")
        }

        val message = buildString {
            appendLine("## 💰 Premium")
            appendLine(descriptionPrefix)
            appendLine(descriptionSlash)
        }

        val embed = EmbedBuilder()
            .setThumbnail("https://emoji.discadia.com/emojis/ae3fa23f-4953-4b07-b892-b7fe0d221ebb.PNG")
            .setDescription(message)
            .setColor(Color.WHITE)
            .setFooter("Acima você poderar ver todos meus comandos para pessoas premium", event.jda.selfUser.avatarUrl)
            .build()

        val buttonBack = Button.secondary("button_back_help_menu", "Voltar")

        event.editMessageEmbeds(embed).setActionRow(buttonBack).queue()

    }

    private fun handlerOptionModules(event: StringSelectInteractionEvent) {
        val toggleOn = config.getEmoji("toggle_on")
        val toggleOff = config.getEmoji("toggle_off")
        val botName = event.jda.selfUser.name

        val prefixCommandsActive = arePrefixCommandsActive(event.guild!!.id)
        val ia = if (isActive(event.guild!!.id)) "$toggleOn **Ativado**" else "$toggleOff **Desativado**"
        val prefixActive = if (prefixCommandsActive) "$toggleOn **Ativado**" else "$toggleOff **Desativado**"
        val games = if (isGameActive(event.guild!!.id)) "$toggleOn **Ativado**" else "$toggleOff **Desativado**"


        val description = buildString {
            appendLine("## :gear: Módulos")
            appendLine("## Abaixo direi um breve resumo sobre meus módulos")
            appendLine("- $prefixActive | ``@${botName} prefix commands on`` (Serve para ativar meus comandos por prefixo, caro queira desativar basta trocar o **on** por **off**)")
            appendLine("- $ia | ``@$botName enable ia`` (Com este comando ativado pessoas com meu **Premium** poderam fazer o uso de uma IA apenas mencionando o bot e mandando algo após a menção. Para desativar basta trocar o ``enable`` por ``disable``)")
            appendLine("- $games | ``@$botName enable games`` (Este comando ativa meu módulo de jogos para entreter os membrs do servidor. Para desativar é so trocar o ``enable`` por ``disable``)")
            appendLine("## Extra:")
            appendLine("- ``@$botName set prefix <prefix>`` (Este serve para definir um novo prefixo para o servidor)")
        }

        val embed = EmbedBuilder()
            .setThumbnail("https://emoji.discadia.com/emojis/93cd68a0-5f10-40fc-9267-28127e45f02f.PNG")
            .setDescription(description)
            .setColor(Color.WHITE)
            .setFooter("Acima você poderar ver todos meus módulos", event.jda.selfUser.avatarUrl)
            .build()

        val buttonBack = Button.secondary("button_back_help_menu", "Voltar")

        event.editMessageEmbeds(embed).setActionRow(buttonBack).queue()
    }

    private fun isActive(guildId: String): Boolean {
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, "modules")

        val filter = Document("guild_id", guildId)
        val result = collection?.find(filter)?.firstOrNull()

        return result?.getBoolean("conversationEnabled") ?: false
    }

    private fun isGameActive(guildId: String): Boolean {
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, "guild_settings")

        val filter = Document("guild_id", guildId)
        val result = collection?.find(filter)?.firstOrNull()

        return result?.getBoolean("active_games") ?: false
    }
}
