package com.github.feeling.src.database.utils

import com.github.feeling.src.database.Database
import org.bson.Document

private val db = Database.instance

fun getTagDocument(guildId: String): Document? {
    val database = db.client?.getDatabase("Feeling")
    val collection = getOrCreateCollection(database, "tags")
    return collection?.find(Document("guildId", guildId))!!.firstOrNull()
}

fun getTagsArray(tagDocument: Document?): List<Document>? {
    return if (tagDocument != null && tagDocument.contains("tags")) {
        val tags = tagDocument["tags"]
        if (tags is List<*>) {
            tags.filterIsInstance<Document>()
        } else {
            null
        }
    } else {
        null
    }
}