package com.github.feeling.src.components.buttons.premium.vt

import com.github.feeling.src.components.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class VirusTotalBackEmbed : Button {
    override val id: String = "back_embed_vt"
    override val action: (ButtonInteractionEvent) -> Unit = {event ->
        VirusTotalComponent().handleBackToEmbed(event)
    }
}