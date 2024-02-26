package com.github.feeling.src.database.utils

import com.github.feeling.src.database.Database
import com.github.feeling.src.database.schema.Guild
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.Document

private val db = Database.instance

fun activePrefixCommands(guild: Guild, active: Boolean) {
    val database = Database.instance.client?.getDatabase(Database.instance.databaseName)
    val collection = getOrCreateCollection(database, "guilds")

    val filter = Document("guild_id", guild.guildId)
    val update = Updates.set("prefix_commands", active)
    val options = UpdateOptions().upsert(true)

    collection?.updateOne(filter, update, options)
}

fun arePrefixCommandsActive(guildId: String): Boolean {
    val database = Database.instance.client?.getDatabase(Database.instance.databaseName)
    val collection = getOrCreateCollection(database, "guilds")

    val filter = Document("guild_id", guildId)
    val result = collection?.find(filter)?.firstOrNull()

    return result?.getBoolean("prefix_commands") ?: false
}

fun getPrefix(guild: Guild): String? {
    val database = Database.instance.client?.getDatabase(Database.instance.databaseName)
    val collection = getOrCreateCollection(database, "guilds")

    val filter = Document("guild_id", guild.guildId)
    val projection = Document("_id", 0).append("prefix", 1)

    val result = collection?.find(filter)?.projection(projection)?.first()
    return result?.getString("prefix")
}

fun updatePrefix(guild: Guild, newPrefix: String) {
    val database = Database.instance.client?.getDatabase(Database.instance.databaseName)
    val collection = getOrCreateCollection(database, "guilds")

    val filter = Document("guild_id", guild.guildId)
    val update = Updates.set("prefix", newPrefix)
    val options = UpdateOptions().upsert(true)

    collection?.updateOne(filter, update, options)
}
