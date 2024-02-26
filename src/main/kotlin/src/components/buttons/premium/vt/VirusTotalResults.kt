package com.github.feeling.src.components.buttons.premium.vt

import com.github.feeling.src.components.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class VirusTotalResults : Button {
    override val id: String = "view_results"
    override val action: (ButtonInteractionEvent) -> Unit = { evemt ->
        VirusTotalComponent().handleViewResults(evemt)
    }
}