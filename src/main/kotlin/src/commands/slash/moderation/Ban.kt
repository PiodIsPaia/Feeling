package com.github.feeling.src.commands.slash.moderation

import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.awt.Color

class Ban : SlashCommandData(
    name = "ban",
    description = "[ Moderação ] Um simples comando para banir alguém do servidor",
    permission = arrayOf(Permission.BAN_MEMBERS),
    options = listOf(
        Option(
            name = "user",
            description = "Mencione a pessoa a ser banida",
            type = OptionType.USER,
            required = true)
    ),
    run = { interaction ->
        HandleBan.ban(interaction)
    }
)

object HandleBan {
    fun ban(event: SlashCommandInteractionEvent) {
        val user = event.getOption("user")!!.asUser

        val embed = EmbedBuilder()
            .setTitle("``${event.user.name}`` Você confirma o banimento de ``${user.name}`` ?")
            .setColor(Color.decode("#2b2d31"))
            .build()

        val buttonConfirm = Button.of(ButtonStyle.SUCCESS, "button_confirm_ban", "Confirmar", Emoji.fromFormatted("✔"))
        val buttonCancel = Button.of(ButtonStyle.DANGER, "button_cancel_ban", "Cancelar", Emoji.fromFormatted("❌"))

        event.replyEmbeds(embed)
            .setEphemeral(true)
            .setActionRow(buttonConfirm, buttonCancel)
            .queue()
    }
}