package com.github.feeling.src.commands.prefix.economy

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document
import java.time.LocalDate
import kotlin.random.Random

class Daily : ListenerAdapter() {
    private val db = Database.instance

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val prefix = getPrefix(event.guild) ?: Bot().prefix

        if (event.message.contentRaw == "${prefix}daily") {
            val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

            if (prefixCommandsActive) {
                val database = db.client?.getDatabase("Feeling")
                val collection = getOrCreateCollection(database, "wallet")

                val userId = event.author.id
                val userName = event.author.name

                val lastClaimDate = collection?.let { getUserLastClaimDate(it, userId) }
                val currentDate = LocalDate.now()

                if (lastClaimDate == currentDate) {
                    event.message.reply("Você já resgatou o prêmio diário hoje.").queue()
                    return
                }

                val random = Random.nextInt(3000)

                val filter = Document("user_id", userId)
                val update = Document("\$inc", Document("balance", random))
                    .append("\$set", Document("lastDailyClaim", currentDate.toString()).append("username", userName))
                val options = UpdateOptions().upsert(true)

                collection?.updateOne(filter, update, options)

                event.message.reply("Você, $userName, recebeu **$random milhos** 🌽 no seu ``daily``").queue()
            } else {
                return
            }
        }
    }

    private fun getUserLastClaimDate(collection: MongoCollection<Document>, userId: String): LocalDate? {
        val filter = Document("user_id", userId)
        val projection = Document("_id", 0).append("lastDailyClaim", 1)
        val result = collection.find(filter).projection(projection).first()

        return result?.get("lastDailyClaim")?.let { LocalDate.parse(it as String) }
    }
}
