package com.github.feeling.src.commands.slash.utils

import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.config.Config
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.awt.Color

class Avatar : SlashCommandData(
    name = "avatar",
    description = "[ Utilidade ] Veja o avatar de alguém.",
    options = listOf(
        Option(
            name = "user",
            description = "Mencione o usuario para eu pegar o avatar",
            type = OptionType.USER
        )
    ),
    run = { event ->
        HandlerAvatar.executeSlash(event)
    }
)

object HandlerAvatar {
    private val config = Config()

    fun executeSlash(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        val getUser = event.getOption("user")?.asUser

        val user = getUser ?: event.user

        event.jda.retrieveUserById(user.id).queue { member ->

            val avatar = member.avatarUrl + "?size=1024"

            val embed = EmbedBuilder()
                .setColor(Color.decode(config.colorEmbed))
                .setImage(avatar)
                .build()

            event.hook.editOriginalEmbeds(embed).queue()
        }
    }
}