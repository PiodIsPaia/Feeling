package com.github.feeling.src.components.buttons.ban

import com.github.feeling.src.components.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class CancelBan : Button {
    override val id: String = "button_cancel_ban"
    override val action: (ButtonInteractionEvent) -> Unit = { event ->
        handleCancelBan(event)
    }

    private fun handleCancelBan(event: ButtonInteractionEvent) {
        event.editMessage("❤ | Prontinho, cancelei a ação!").setEmbeds().setComponents().queue()
    }
}