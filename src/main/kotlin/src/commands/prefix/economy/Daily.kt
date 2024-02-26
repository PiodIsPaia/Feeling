package com.github.feeling.src.commands.prefix.economy

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.schema.User
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import com.github.feeling.src.database.utils.users.Users
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.client.model.ValidationOptions
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

class Daily : ListenerAdapter() {

    fun execute(event: MessageReceivedEvent) {
        val collection = Users().getOrCreateCollectionUser("users")

        val userId = event.author.id
        val userName = event.author.name

        val user = collection?.find(Filters.eq("user_id", userId))?.first()

        if (user != null) {
            val lastClaimUnixMillis = user.wallet.lastClaimDaily

            val lastClaimInstant = Instant.ofEpochMilli(lastClaimUnixMillis)
            val lastClaimDateTime = ZonedDateTime.ofInstant(lastClaimInstant, ZoneId.systemDefault())

            val currentInstant = Instant.now()
            val currentDateTime = ZonedDateTime.ofInstant(currentInstant, ZoneId.systemDefault())

            if (lastClaimDateTime.toLocalDate() == currentDateTime.toLocalDate()) {
                event.message.reply("Você já resgatou o prêmio diário hoje.").queue()
                return
            }
        }

        val random = Random.nextInt(3000)
        val currentUnixTimeMillis = System.currentTimeMillis()

        val premiumMultiplier = if (user?.premium?.active == true) 1.3 else 1.0
        val milhosEarned = (random * premiumMultiplier).toInt()

        val update = Updates.combine(
            Updates.inc("wallet.milhos", milhosEarned),
            Updates.set("wallet.lastClaimDaily", currentUnixTimeMillis),
            Updates.set("username", userName)
        )
        val options = UpdateOptions().upsert(true)

        collection?.updateOne(Filters.eq("user_id", userId), update, options)

        val extraMilhosMessage = if (user?.premium?.active == true) {
            val extraMilhos = (milhosEarned - random)
            " (Você ganhou: **$extraMilhos** milhos extras por ser premium ⭐)"
        } else {
            ""
        }

        event.message.reply("Você, ``$userName``, recebeu **$milhosEarned milhos** 🌽 no seu ``daily``$extraMilhosMessage").queue()
    }
}
