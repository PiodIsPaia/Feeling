package com.github.feeling.src.components.moderation.ban

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class BanComponent : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when (event.button.id) {
            "button_confirm_ban" -> handleConfirmBan(event)
            "button_cancel_ban" -> handleCancelBan(event)
        }
    }

    private fun handleCancelBan(event: ButtonInteractionEvent) {
        event.editMessage("> ❤ | Prontinho, cancelei a ação!").setEmbeds().setComponents().queue()
    }

    private fun handleConfirmBan(event: ButtonInteractionEvent) {

    }
}