package com.github.feeling.src.commands.slash.premium

import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.commands.slash.SubCommand
import com.github.feeling.src.config.Config
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class Premium : SlashCommandData(
    name = "premium",
    description = "[ Premium ] Vizualize ou compre o meu premium para me ajduar a ficar online.",
    subcommands = listOf(
        SubCommand(
            name = "comprar",
            description = "[ Bot ] Compre meu premium e me ajude a ficar online. "
        ),
    ),
    run = {event ->
        when (event.subcommandName) {
            "comprar" -> HandlerPremium.buy(event)
        }
    }
)

object HandlerPremium {
    private val config = Config()
    private val color = config.colorEmbed

    fun buy(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        val embed = EmbedBuilder()
            .setAuthor(event.user.name, null, event.user.avatarUrl ?: event.jda.selfUser.avatarUrl)
            .setThumbnail("https://i.imgur.com/zNgWl4r.gif")
            .setTitle("Adquira meu premium e desfrute de recursos exclusivos!")
            .setDescription("""
                ## ✨ Benefícios Premium:, 
                - Acesso a recursos exclusivos
                - Suporte prioritário
                - Mais **milhos** no seu **daily**
                - Sem limitações no bot
                
                ## :money_with_wings: Preço:
                - 5 mil **milhos** por **3 dias** de premium
            """.trimIndent())
            .setColor(Color.decode(color))
            .setFooter("Clique no botão abaixo para comprar a assinatura premium.")
            .build()

        val buttonBuy = Button.success("button_buy_premium", "Comprar")
            .withEmoji(Emoji.fromFormatted("💰"))

        event.hook.editOriginalEmbeds(embed).setActionRow(buttonBuy).queue()
    }
}
