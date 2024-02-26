package com.github.feeling.src.commands.prefix.admin

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.schema.Guild
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document
import java.util.*
import kotlin.concurrent.schedule

class AddPremium : ListenerAdapter() {
    private val db = Database.instance

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val config = Config()
        val content = event.message.contentRaw
        val prefix = getPrefix(Guild(event.guild.id, event.guild.name)) ?: config.prefix

        val regex = Regex("""${Regex.escape(prefix)}add premium -user ?(\d+) -duration (\d+)\s*(\w+)""")
        val matchResult = regex.find(content) ?: return

        if (event.author.id != dotenv()["BOT_OWNER_ID"]) return

        val targetUserId = matchResult.groups[1]?.value?.toLongOrNull() ?: return
        val duration = matchResult.groups[2]?.value?.toIntOrNull() ?: return
        val timeUnit = matchResult.groups[3]?.value ?: return

        event.jda.retrieveUserById(targetUserId).queue { user ->
            val userId = user.id
            val database = db.client?.getDatabase(db.databaseName)
            val collection = getOrCreateCollection(database, "users")

            val existingUser = collection?.find(Document("user_id", userId))?.firstOrNull()
            if (existingUser == null) {
                event.channel.sendMessage("O usuário ${user.asMention} não foi encontrado.").queue()
                return@queue
            }

            val premium = existingUser["premium"] as? Document
            if (premium != null && premium.getBoolean("active") == true) {
                event.channel.sendMessage("O usuário ${user.asMention} já está no grupo Premium.").queue()
                return@queue
            }

            val expirationTime = calculateExpirationTime(duration, timeUnit)

            val premiumDocument = Document("active", true)
                .append("expiration", expirationTime)

            val update = Updates.set("premium", premiumDocument)
            val filter = Filters.eq("user_id", userId)

            collection.updateOne(filter, update)

            event.message.reply("Prontinho! Adicionei o ${user.asMention} à lista de usuários **Premium**.").queue()

            val delay = expirationTime - System.currentTimeMillis()
            Timer().schedule(delay) {
                val unsetPremium = Updates.unset("premium")
                collection?.updateOne(filter, unsetPremium)
                event.channel.sendMessage("O período Premium de ${user.asMention} expirou.").queue()
            }
        }
    }

    private fun calculateExpirationTime(duration: Int, timeUnit: String): Long {
        val now = System.currentTimeMillis()
        val millisecondsInMinute = 60000L
        val millisecondsInHour = 3600000L
        val millisecondsInDay = 86400000L

        return when (timeUnit.lowercase()) {
            "minute", "minutes" -> now + duration * millisecondsInMinute
            "hour", "hours" -> now + duration * millisecondsInHour
            "day", "days" -> now + duration * millisecondsInDay
            else -> throw IllegalArgumentException("Unidade de tempo inválida.")
        }
    }
}
