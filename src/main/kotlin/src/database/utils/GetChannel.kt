package com.github.feeling.src.database.utils

import com.mongodb.client.MongoCollection
import org.bson.Document
import org.bson.conversions.Bson

fun getChannel(collection: MongoCollection<Document>?, filter: Bson, guildIdField: String, channelIdField: String
): Document? {
    val projection = Document("_id", 0).append(guildIdField, 1).append(channelIdField, 1)
    val result = collection?.find(filter)?.projection(projection)
    return result?.firstOrNull()
}