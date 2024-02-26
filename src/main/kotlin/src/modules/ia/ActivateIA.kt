package com.github.feeling.src.modules.ia

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.mongodb.client.model.UpdateOptions
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class ActivateIA : ListenerAdapter() {
    private val db = Database.instance
    private val config = Config()

    private val loading = config.getEmoji("loading")
    private val confirmGif = config.getEmoji("confirm_gif")

    fun enableModule(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.hook.editOriginal("Você não tem permissão para executar esta ação!").queue()
            return
        }

        val message = event.hook.editOriginal("$loading ativando módulo, aguarde uns segundos..").complete()

        handlerActive(event, true)
        Thread.sleep(2000)

        message.editMessage("$confirmGif módulo foi ativado com sucesso!").queue()
    }

    fun disableModule(event: SlashCommandInteractionEvent) {
        event.deferReply().queue()

        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.hook.editOriginal("Você não tem permissão para executar esta ação!").queue()
            return
        }

        val message = event.hook.editOriginal("$loading desativando módulo, aguarde uns segundos..").complete()

        handlerActive(event, false)
        Thread.sleep(2000)

        message.editMessage("$confirmGif módulo desativado com sucesso!").queue()
    }

    private fun handlerActive(event: SlashCommandInteractionEvent, active: Boolean) {
        val guildId = event.guild!!.id

        val database = db.client?.getDatabase(Database.instance.databaseName)
        val collection = getOrCreateCollection(database, "guilds")

        val filter = Document("guild_id", guildId)
        val update = Document("\$set", Document("ia", active))

        collection?.updateOne(filter, update)
    }
}