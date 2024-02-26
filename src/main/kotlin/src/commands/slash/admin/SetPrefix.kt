package com.github.feeling.src.commands.slash.admin

import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.commands.slash.SubCommand
import com.github.feeling.src.database.schema.Guild
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.updatePrefix
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

class SetPrefix : SlashCommandData(
    name = "setar",
    description = "[ Admin ] Sete um novo prefixo para o servidor",
    permission = arrayOf(Permission.ADMINISTRATOR),
    subcommands = listOf(
        SubCommand(
            name = "prefixo",
            description = "[ Admin ] Escolha um novo prefixo para eu usar neste servidor",
            options = listOf(
                Option(
                    name = "prefixo",
                    description = "Novo prefixo",
                    type = OptionType.STRING,
                    required = true
                )
            )
        )
    ),
    run = {event ->
        when (event.subcommandName) {
            "prefixo" -> HandlerSetPrefix.execute(event)
        }
    }
)

object HandlerSetPrefix {
    fun execute(event: SlashCommandInteractionEvent) {
        val prefixOption = event.getOption("prefixo")?.asString ?: return

        val prefixCommandsActive = arePrefixCommandsActive(event.guild!!.id)

        if (!prefixCommandsActive) {
            event.reply("Para usar este comando, você precisa permitir que eu possa utilizar \"prefix commands\" neste servidor. Para ativar meus comandos por prefixo, use o seguinte comando: `@Feeling prefix commands on`.")
                .setEphemeral(true).queue()
            return
        }

        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Você não tem permissão para executar esta ação!").setEphemeral(true).queue()
            return
        }

        when {
            prefixOption.length > 3 -> {
                event.reply("Você precisa definir um prefixo que tenha **3** ou menos caracteres!").setEphemeral(true).queue()
            }
            prefixOption.isNotBlank() -> {
                updatePrefix(Guild(event.guild!!.id, event.guild!!.name), prefixOption)
                event.reply("O prefixo foi atualizado para '$prefixOption'.").queue()
            }
            else -> event.reply("Você precisa especificar um novo prefixo.").setEphemeral(true).queue()
        }
    }
}
