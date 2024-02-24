package com.github.feeling.src.commands.prefix.games

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.mongodb.client.model.UpdateOptions
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class ActiveGames : ListenerAdapter() {
    private val config = Config()
    private val loading = config.getEmoji("loading")
    private val confirmGif = config.getEmoji("confirm_gif")

    private val db = Database.instance
    private val guildSettingCollectionName = "guild_settings"
    private val activeGamesField = "active_games"

    fun execute(event: MessageReceivedEvent, enable: Boolean) {
        if (enable) {
            val message = event.message.reply("$loading Ativando meu módulo de jogos...").complete()

            updateActiveGames(event.guild.id, true)

            Thread.sleep(2000)

            message.editMessage("$confirmGif Módulo de jogos foi ativado.").queue()
        } else {

            val message = event.message.reply("$loading Desativando meu módulo de jogos...").complete()

            updateActiveGames(event.guild.id, false)

            Thread.sleep(2000)

            message.editMessage("$confirmGif Módulo de jogos foi desativado.").queue()
        }
    }

    private fun updateActiveGames(guildId: String, active: Boolean) {
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, guildSettingCollectionName)

        val filter = Document("guild_id", guildId)
        val update = Document("\$set", Document(activeGamesField, active))

        collection?.updateOne(filter, update, UpdateOptions().upsert(true))
    }
}
