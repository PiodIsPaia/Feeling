package com.github.feeling.src.database.utils

import com.github.feeling.src.database.Database
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.Document

private val db = Database.instance

fun activePrefixCommands(event: MessageReceivedEvent, active: Boolean) {
    val database = db.client?.getDatabase("Feeling")
    val collection = getOrCreateCollection(database, "guild_settings")

    val guildID = event.guild.id

    val filter = Document("guild_id", guildID)
    val update = Document("\$set", Document("active_prefix_commands", active))
    val options = UpdateOptions().upsert(true)

    collection?.updateOne(filter, update, options)
}

fun arePrefixCommandsActive(guildId: String): Boolean {
    val database = db.client?.getDatabase("Feeling")
    val collection = getOrCreateCollection(database, "guild_settings")

    val filter = Document("guild_id", guildId)
    val result = collection?.find(filter)?.firstOrNull()

    return result?.getBoolean("active_prefix_commands") ?: false
}

fun getPrefix(guild: Guild): String? {
    val database = db.client?.getDatabase("Feeling")
    val collection = getOrCreateCollection(database, "guild_settings")

    val guildId = guild.id
    val filter = Document("guild_id", guildId)
    val projection = Document("_id", 0).append("prefix", 1)

    val result = collection?.find(filter)?.projection(projection)?.first()
    return result?.getString("prefix")
}

fun updatePrefix(guild: Guild, newPrefix: String) {
    val database = Database.instance.client?.getDatabase("Feeling")
    val collection = getOrCreateCollection(database, "guild_settings")

    val guildId = guild.id
    val filter = Filters.eq("guild_id", guildId)
    val update = Document("\$set", Document("prefix", newPrefix))
    val options = UpdateOptions().upsert(true)

    collection?.updateOne(filter, update, options)
}