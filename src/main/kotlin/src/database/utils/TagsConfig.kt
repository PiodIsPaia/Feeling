package com.github.feeling.src.database.utils

import com.github.feeling.src.database.Database
import com.mongodb.client.model.Filters
import org.bson.Document

private val db = Database.instance

fun getTagDocument(guildId: String): Document? {
    val database = db.client?.getDatabase("Feeling")
    val collection = getOrCreateCollection(database, "guilds")
    return collection?.find(Filters.eq("guild_id", guildId))?.firstOrNull()
}

fun getTagsArray(tagDocument: Document?): List<Document>? {
    return tagDocument?.getList("tags", Document::class.java)
}
