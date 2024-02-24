package com.github.feeling.src.commands.prefix.economy

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class Wallet : ListenerAdapter() {
    private val db = Database.instance
     fun execute(event: MessageReceivedEvent) {
        val mentionedMembers = event.message.mentions.members

        if (mentionedMembers.isNotEmpty()) {
            val mentionedMember = mentionedMembers[0]
            val balance = getBalance(mentionedMember.id)
            event.message.reply("${mentionedMember.asMention}, o saldo atual é: **$balance milhos** 🌽").queue()
        } else {
            val balance = getBalance(event.author.id)
            event.message.reply("Seu saldo atual é: **$balance milhos** 🌽").queue()
        }
    }

    private fun getBalance(userId: String): Int {
        val database = db.client?.getDatabase("Feeling")
        val wallet = getOrCreateCollection(database, "wallet")

        val filter = Document("user_id", userId)
        val projection = Document("_id", 0).append("balance", 1)

        val result = wallet?.find(filter)?.projection(projection)?.first()
        return result?.getInteger("balance") ?: 0
    }
}
