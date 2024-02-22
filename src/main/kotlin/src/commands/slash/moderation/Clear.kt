package com.github.feeling.src.commands.slash.moderation

import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class Clear : SlashCommandData(
    name = "limpar",
    description = "[ Moderação ] Apague mensagens do chat caso precise.",
    options = listOf(
        Option(
            type = OptionType.STRING,
            name = "quantidade",
            description = "Quantidade de mensagens a serem apagadas(maximo: 1000)",
            required = true
        )
    ),
    permission = arrayOf(Permission.MESSAGE_MANAGE),
    run = {event ->
        HandlerClear.clear(event)
    }
)

object HandlerClear {
    fun clear(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        val amount = event.getOption("quantidade")?.asString?.toIntOrNull() ?: run {
            event.hook.editOriginal("Por favor, forneça um número válido de mensagens a serem apagadas.").queue()
            return
        }

        if (amount !in 1..1000) {
            event.hook.editOriginal("A quantidade de mensagens deve estar entre **1** e **1000**w .").queue()
            return
        }

        event.channel.iterableHistory.takeAsync(amount).thenAccept { messages ->
            val oldMessages = messages.filter { ChronoUnit.DAYS.between(it.timeCreated, OffsetDateTime.now()) > 7 }
            val remainingMessages = messages - oldMessages.toSet()

            if (oldMessages.isNotEmpty()) {
                val oldMessagesCount = oldMessages.size
                event.hook.editOriginal("Não é possível excluir mensagens enviadas há mais de 7 dias. Foram encontradas $oldMessagesCount mensagens antigas.").queue()
            }

            if (remainingMessages.isNotEmpty()) {
                event.channel.purgeMessages(remainingMessages)
                event.hook.editOriginal("Foram apagadas **${remainingMessages.size}** mensagens.").queue()
            }
        }
    }
}