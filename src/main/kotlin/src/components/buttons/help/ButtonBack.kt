package com.github.feeling.src.components.buttons.help

import com.github.feeling.src.components.Button
import com.github.feeling.src.components.selectMenu.help.Help
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ButtonBack : Button {
    override val id: String = "button_back_help_menu"
    override val action: (ButtonInteractionEvent) -> Unit = {event ->
        val help = Help()

        help.buttonBack(event)
    }
}