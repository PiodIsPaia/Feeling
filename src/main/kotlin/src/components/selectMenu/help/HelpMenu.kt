package com.github.feeling.src.components.selectMenu.help

import com.github.feeling.src.components.StringSelectMenu
import com.github.feeling.src.config.Config
import com.github.feeling.src.database.schema.Guild
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class HelpMenu : StringSelectMenu {
    override val id: String = "menu_help_command"
    override val action: (StringSelectInteractionEvent) -> Unit = {event ->
        val selectedValues = event.values

        val help = Help()
        val config = Config()

        val prefix = event.guild?.let { getPrefix(Guild(event.guild!!.id, event.guild!!.name)) } ?: config.prefix

        for (option in selectedValues) {
            when (option) {
                "menu_help_economy" -> help.handlerOptionEconomy(event, prefix)
                "menu_help_mod" -> help.handlerOptionMod(event, prefix)
                "menu_help_fun" -> help.handlerOptionFun(event, prefix)
                "menu_help_premium" -> help.handlerOptionPremium(event, prefix)
                "menu_help_modules" -> help.handlerOptionModules(event)
            }
        }
    }
}