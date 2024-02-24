package com.github.feeling.src.commands.prefix


import com.github.feeling.src.config.Config
import com.github.feeling.src.database.utils.activePrefixCommands
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class EnablePrefixCommands : ListenerAdapter() {
    private val config = Config()

    private val loading = config.getEmoji("loading")
    private val confirmGif = config.getEmoji("confirm_gif")

    fun execute(event: MessageReceivedEvent, enable: Boolean) {
        if (enable) {
            if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
                event.message.reply("Você não tem permissão para executar esta ação!").queue()
                return
            }

            val msg = event.message.reply("$loading **| Ativando, aguarde uns segundos!**").complete()

            activePrefixCommands(event, true)

            Thread.sleep(2000)
            msg.editMessage("$confirmGif **| Comandos por prefixo foram ativados.**").queue()
            return
        } else {
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

