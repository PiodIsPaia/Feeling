package com.github.feeling

import com.github.feeling.src.core.Client
import com.github.feeling.src.database.Database
import io.github.cdimascio.dotenv.dotenv

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val dotenv = dotenv()
    val token = dotenv["TOKEN"]
    val mongodbUri = dotenv["MONGO_URI"]

    val client = Client()
    val database = Database.instance

    client.run(token = token)
    database.connect(mongodbUri)
}