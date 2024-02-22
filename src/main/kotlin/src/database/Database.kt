package com.github.feeling.src.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import java.util.logging.Level
import java.util.logging.Logger

class Database private constructor() {
    companion object {
        val instance by lazy { Database() }
    }

    var client: MongoClient? = null
    val databaseName: String = "Feeling"

    fun connect(uri: String): MongoDatabase? {
        return try {
            val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(uri))
                .retryWrites(true)
                .build()

            client = MongoClients.create(settings)
            client!!.listDatabaseNames()

            println("Conectado ao MongoDB")
            client!!.getDatabase(databaseName)
        } catch (e: Exception) {
            Logger.getLogger("Connection").log(Level.SEVERE, "Erro ao conectar ao MongoDB: ${e.message}", e)
            null
        }
    }
}