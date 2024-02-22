package com.github.feeling.src.commands.prefix

import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.updatePrefix
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class SetPrefix : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val botMention = event.jda.selfUser.asMention
        val messageContent = event.message.contentRaw

        when {
            messageContent.startsWith("$botMention set prefix") -> {
                val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

                if (prefixCommandsActive) {
                    if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
                        event.message.reply("Você não tem permissão para executar esta ação!").queue()
                        return
                    }

                    val newPrefix = messageContent.substringAfterLast("set prefix").trim()

                    when {
                        newPrefix.length > 3 -> {
                            event.message.reply("Você precisa definir um prefixo que tenha **3** ou menos caracteres**!").queue()
                            return
                        }
                        newPrefix.isNotBlank() -> {
                            updatePrefix(event.guild, newPrefix)
                            event.message.reply("O prefixo foi atualizado para '$newPrefix'.").queue()
                        }
                        else -> event.message.reply("Você precisa especificar um novo prefixo.").queue()
                    }
                } else {
                    event.message.reply("Para usar este comando precisa permitir que eu possa utilizar ``prefix commands`` neste servidor. Para fazer a ativação dos meus comandos por prefixo use o seguinte comando: ``@Feeling prefix commands on``.").queue()
                }
            }
        }
    }
}
