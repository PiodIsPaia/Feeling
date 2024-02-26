package com.github.feeling.src.commands.slash.admin

import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.commands.slash.SubCommand
import com.github.feeling.src.modules.ia.ActivateIA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.OptionType

class EnableIA : SlashCommandData(
    name = "ia",
    description = "[ Admin ] Ative ou desative meu módulo de IA",
    permission = arrayOf(Permission.ADMINISTRATOR),
    subcommands = listOf(
        SubCommand(
            name = "toggle",
            description = "[ Admin ] Ative ou desative meu módulo de IA",
            options = listOf(
                Option(
                    name = "ativar",
                    description = "Alterne entre true ou false para ativar ou desativar",
                    type = OptionType.BOOLEAN,
                    required = true
                )
            )
        )
    ),
    run = {event ->
        val enable = event.getOption("ativar")!!.asBoolean
        val activateIA = ActivateIA()

        if (enable) {
            activateIA.enableModule(event)
        } else {
            activateIA.disableModule(event)
        }
    }
)