package com.github.feeling.src.commands.slash.admin


import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.commands.slash.SubCommand
import com.github.feeling.src.commands.slash.SubCommandGroup
import com.github.feeling.src.config.Config
import com.github.feeling.src.database.schema.Guild
import com.github.feeling.src.database.utils.activePrefixCommands
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

class EnablePrefixCommands : SlashCommandData(
    name = "prefix",
    description = "[ Admin ] Ativação ou desativação dos comandos por prefixo",
    permission = arrayOf(Permission.ADMINISTRATOR),
    subcommandsGroup = listOf(
        SubCommandGroup(
            name = "commands",
            description = "[ Admin ] Configure meus comandos por prefixo",
            subcommands = listOf(
                SubCommand(
                    name = "toggle",
                    description = "[ Admin ] Alterna a ativação dos comandos por prefixo",
                    options = listOf(
                        Option(
                            type = OptionType.BOOLEAN,
                            name = "ativar",
                            description = "Ativar ou desativar meus comandos por prefixo",
                            required = true
                        )
                    )
                )
            )
        )
    ),
    run = { event ->
        val subCommandGroup = event.subcommandGroup
        val subCommandName = event.subcommandName

        if (subCommandGroup == "commands" && subCommandName == "toggle") {
            val activate = event.getOption("ativar")!!.asBoolean
            HandlerPrefix.togglePrefixCommands(event, activate)
        }
    }
)
object HandlerPrefix {

    private val config = Config()

    private val loading = config.getEmoji("loading")
    private val confirmGif = config.getEmoji("confirm_gif")

    fun togglePrefixCommands(event: SlashCommandInteractionEvent, enable: Boolean) {
        val actionText = if (enable) "Ativando" else "Desativando"
        val actionResultText = if (enable) "ativados" else "desativados"

        val msg = event.reply("$loading **| $actionText, aguarde uns segundos!**").complete()

        activePrefixCommands(Guild(event.guild!!.id, event.guild!!.name), enable)

        Thread.sleep(2000)
        msg.editOriginal("$confirmGif **| Comandos por prefixo foram $actionResultText.**").queue()
    }
}
