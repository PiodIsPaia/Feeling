package com.github.feeling.src.components

import com.github.feeling.src.commands.prefix.utils.VirusTotal
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button

class VirusTotalComponent : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when (event.componentId) {
            "view_results" -> handleViewResults(event)
            "back_embed_vt" -> handleBackToEmbed(event)
        }
    }

    private fun handleViewResults(event: ButtonInteractionEvent) {
        val file = VirusTotal.scannedFile
        val report = VirusTotal.analysisInfo

        if (file != null && report != null) {
            val embed = VirusTotal().parseScanResults(report)
            val button = Button.secondary("back_embed_vt", "Voltar")

            event.editMessageEmbeds(embed).setContent(null).setActionRow(button).queue()
        } else {
            event.reply("O arquivo não foi analisado ainda ou os resultados não estão disponíveis.").queue()
        }
    }

    private fun handleBackToEmbed(event: ButtonInteractionEvent) {
        val embed = VirusTotal.embed
        val button = VirusTotal.button

        event.editMessageEmbeds(embed).setActionRow(button).queue()
    }
}
