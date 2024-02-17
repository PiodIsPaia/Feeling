package com.github.feeling.src.commands.prefix

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.activePrefixCommands
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.mongodb.client.model.UpdateOptions
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class PrefixCommands : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.message.reply("Você não tem permissão para executar esta ação!").queue()
            return
        }

        val loading = Bot().getEmoji("loading")
        val confirmGif = Bot().getEmoji("confirm_gif")

        when (event.message.contentRaw) {
            "${event.jda.selfUser.asMention} prefix commands on" -> {
                val msg = event.message.reply("$loading **| Ativando, aguarde uns segundos!**").complete()

                activePrefixCommands(event, true)

                Thread.sleep(2000)
                msg.editMessage("$confirmGif **| Comandos por prefixo foram ativados.**").queue()
            }
            "${event.jda.selfUser.asMention} prefix commands off" -> {
                val msg = event.message.reply("$loading **| Desativando, aguarde uns segundos!**").complete()

                activePrefixCommands(event, false)

                Thread.sleep(2000)
                msg.editMessage("$confirmGif **| Comandos por prefixo foram desativados.**").queue()
            }
        }
    }
}
