package com.github.feeling.src.database.utils

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

fun getOrCreateCollection(database: MongoDatabase?, collectionName: String): MongoCollection<Document>? {
    val existingCollectionNames = database?.listCollectionNames()?.toList() ?: emptyList()

    return if (existingCollectionNames.contains(collectionName)) {
        database?.getCollection(collectionName)
    } else {
        database?.createCollection(collectionName)
        database?.getCollection(collectionName)
    }
}