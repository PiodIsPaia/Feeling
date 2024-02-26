package com.github.feeling.src.commands.prefix.economy

import com.github.feeling.src.commands.prefix.PrefixCommandBuilder
import com.github.feeling.src.database.utils.users.Users
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

class Daily : PrefixCommandBuilder {
    override val name: String = "daily"
    override val aliases: Array<String> = arrayOf()
    override val action: (MessageReceivedEvent) -> Unit = {event ->
        execute(event)
    }

    private fun execute(event: MessageReceivedEvent) {
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
