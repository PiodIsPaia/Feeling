package com.github.feeling.src.commands.prefix.economy

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class Wallet : ListenerAdapter() {
    private val db = Database.instance

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val prefix = getPrefix(event.guild) ?: Bot().prefix

        if (event.message.contentRaw == "${prefix}saldo") {
            val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)
            if (prefixCommandsActive) {
                val database = db.client?.getDatabase("Feeling")
                val wallet = getOrCreateCollection(database, "wallet")

                val userId = event.author.id
                val filter = Document("user_id", userId)
                val projection = Document("_id", 0).append("balance", 1)

                val result = wallet?.find(filter)?.projection(projection)?.first()
                val balance = result?.getInteger("balance") ?: 0

                event.message.reply("Seu saldo atual é: **$balance milhos** 🌽").queue()
            } else {
                return
            }
        }
    }
}
