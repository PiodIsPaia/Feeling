package com.github.feeling

import com.github.feeling.src.core.Client
import com.github.feeling.src.database.Database
import io.github.cdimascio.dotenv.dotenv


fun main() {
    val dotenv = dotenv()
    val token = dotenv["TOKEN"]
    val mongodbUri = dotenv["MONGO_URI"]

    val client = Client()
    val database = Database.instance

    client.run(token = token)
    database.connect("mongodb://localhost:27017")
}