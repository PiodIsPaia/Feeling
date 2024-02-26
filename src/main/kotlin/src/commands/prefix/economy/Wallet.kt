package com.github.feeling.src.commands.prefix.economy

import com.github.feeling.src.commands.prefix.PrefixCommandBuilder
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.Document
import java.time.*

class Wallet : PrefixCommandBuilder {
    override val name: String = "saldo"
    override val aliases: Array<String> = arrayOf("wallet")
    override val action: (MessageReceivedEvent) -> Unit = {event ->
        execute(event)
    }

    private val db = Database.instance

    private fun execute(event: MessageReceivedEvent) {
        val mentionedMembers = event.message.mentions.members

        if (mentionedMembers.isNotEmpty()) {
            val mentionedMember = mentionedMembers[0]
            val userInfo = getUserInfo(mentionedMember.id)
            val nextClaimFormatted = formatNextClaimTime(userInfo.second, userInfo.third)
            event.message.reply("${mentionedMember.asMention}, o saldo atual é: **${userInfo.first} milhos** 🌽\nPróximo claim diário disponível em: $nextClaimFormatted").queue()
        } else {
            val userInfo = getUserInfo(event.author.id)
            val nextClaimFormatted = formatNextClaimTime(userInfo.second, userInfo.third)
            event.message.reply("Seu saldo atual é: **${userInfo.first} milhos** 🌽\nPróximo claim diário disponível em: $nextClaimFormatted").queue()
        }
    }

    private fun getUserInfo(userId: String): Triple<Int, ZonedDateTime?, Duration?> {
        val database = db.client?.getDatabase(db.databaseName)
        val usersCollection = getOrCreateCollection(database, "users")

        val filter = Document("user_id", userId)
        val projection = Document("_id", 0)
            .append("wallet.milhos", 1)
            .append("wallet.lastClaimDaily", 1)
            .append("premium.expiration", 1)

        val result = usersCollection?.find(filter)?.projection(projection)?.first()
        val walletDocument = result?.get("wallet") as? Document
        val balance = walletDocument?.getInteger("milhos") ?: 0
        val lastClaimDaily = if (balance > 0) {
            walletDocument?.getLong("lastClaimDaily")?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.of("America/Sao_Paulo")).plusHours(24)
            }
        } else {
            null
        }

        val premiumDocument = result?.get("premium") as? Document
        val premiumExpiration = premiumDocument?.getLong("expiration")
        val premiumTimeRemaining = premiumExpiration?.let {
            Duration.ofMillis(it - System.currentTimeMillis())
        }

        return Triple(balance, lastClaimDaily, premiumTimeRemaining)
    }

    private fun formatNextClaimTime(nextClaimTime: ZonedDateTime?, premiumTimeRemaining: Duration?): String {
        val nextClaimFormatted = if (nextClaimTime != null) {
            "<t:${nextClaimTime.toEpochSecond()}:F>"
        } else {
            "N/A"
        }

        val premiumTimeFormatted = premiumTimeRemaining?.let {
            "<t:${Instant.now().plus(it).epochSecond}:R>"
        } ?: "``Não é Premium``"

        return "${nextClaimFormatted}\nSeu **Premium** acabará em: $premiumTimeFormatted"
    }
}
