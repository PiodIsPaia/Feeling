package com.github.feeling.src.components.buttons.ban

import com.github.feeling.src.components.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent


class ConfirmBan: Button {
    override val id: String = "button_confirm_ban"
    override val action: (ButtonInteractionEvent) -> Unit = {evemt ->
        handleConfirmBan(evemt)
    }

    private fun handleConfirmBan(event: ButtonInteractionEvent) {

    }
}
