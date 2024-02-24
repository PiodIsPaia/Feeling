package com.github.feeling.src.modules.`fun`

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.mongodb.client.model.UpdateOptions
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class ActivateIA : ListenerAdapter() {
    private val db = Database.instance
    private val config = Config()

    private val loading = config.getEmoji("loading")
    private val confirmGif = config.getEmoji("confirm_gif")

     fun enableModule(event: MessageReceivedEvent) {
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.message.reply("Você não tem permissão para executar esta ação!").queue()
            return
        }

        val message = event.message.reply("$loading ativando módulo, aguarde uns segundos..").complete()

        handlerActive(event, true)
        Thread.sleep(2000)

        message.editMessage("$confirmGif módulo foi ativado com sucesso!").queue()
    }

    fun disableModule(event: MessageReceivedEvent) {
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.message.reply("Você não tem permissão para executar esta ação!").queue()
            return
        }

        val message = event.message.reply("$loading desativando módulo, aguarde uns segundos..").complete()

        handlerActive(event, false)
        Thread.sleep(2000)

        message.editMessage("$confirmGif módulo desativado com sucesso!").queue()
    }

    private fun handlerActive(event: MessageReceivedEvent, active: Boolean) {
        val guildId = event.guild.id
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, "modules")

        val filter = Document("guild_id", guildId)
        val update = Document("\$set", Document("conversationEnabled", active))
        val options = UpdateOptions().upsert(true)

        collection?.updateOne(filter, update, options)
    }
}
