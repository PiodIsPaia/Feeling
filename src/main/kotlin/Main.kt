package com.github.feeling

import com.github.feeling.src.core.Client
import com.github.feeling.src.database.Database
import io.github.cdimascio.dotenv.dotenv


fun main() {
    val dotenv = dotenv()
    val token = dotenv["TOKEN"]
    val mongodbUri = dotenv["MONGO_URI"]
    val mongodbLocal = "mongodb://localhost:27017/"

    val client = Client()
    val database = Database.instance

    client.run(token)
    database.connect(mongodbLocal)
}