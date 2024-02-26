package com.github.feeling.src.database.utils.users

import com.github.feeling.src.database.schema.User
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider

class Users {
    private val db: MongoDatabase

    init {
        val pojoCodecRegistry = CodecRegistries.fromProviders(
            PojoCodecProvider.builder().automatic(true).build()
        )
        val codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            pojoCodecRegistry
        )
        val client = MongoClients.create()
        db = client.getDatabase("Feeling").withCodecRegistry(codecRegistry)
    }

    fun getOrCreateCollectionUser(collectionName: String): MongoCollection<User>? {
        val collection = db.getCollection(collectionName, User::class.java)

        return collection
    }
    
}
