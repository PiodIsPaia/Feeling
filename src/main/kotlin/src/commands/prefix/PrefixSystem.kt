package com.github.feeling.src.commands.prefix

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.activePrefixCommands
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class PrefixSystem : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val bot = Bot()
        val loading = bot.getEmoji("loading")
        val confirmGif = bot.getEmoji("confirm_gif")

        when (event.message.contentRaw) {
            "${event.jda.selfUser.asMention} prefix commands on" -> {

                if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
                    event.message.reply("Você não tem permissão para executar esta ação!").queue()
                    return
                }

                val msg = event.message.reply("$loading **| Ativando, aguarde uns segundos!**").complete()

                activePrefixCommands(event, true)

                Thread.sleep(2000)
                msg.editMessage("$confirmGif **| Comandos por prefixo foram ativados.**").queue()
            }
            "${event.jda.selfUser.asMention} prefix commands off" -> {

                if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
                    event.message.reply("Você não tem permissão para executar esta ação!").queue()
                    return
                }

                val msg = event.message.reply("$loading **| Desativando, aguarde uns segundos!**").complete()

                activePrefixCommands(event, false)

                Thread.sleep(2000)
                msg.editMessage("$confirmGif **| Comandos por prefixo foram desativados.**").queue()
            }
        }
    }
}
