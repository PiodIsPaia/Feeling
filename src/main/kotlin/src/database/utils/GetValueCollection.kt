package com.github.feeling.src.database.utils

import com.github.feeling.src.database.Database
import org.bson.Document

private val database = Database.instance

fun getValueCollection(guildId: String, databaseName: String, collectionName: String, get: String): String? {
    val database = database.client?.getDatabase(databaseName)
    val collection = database?.getCollection(collectionName)

    val filter = Document("guild_id", guildId)
    val projection = Document("_id", 0).append(get, 1)
    val result = collection?.find(filter)?.projection(projection)
    val data = result?.firstOrNull()

    return data?.getString(get)
}