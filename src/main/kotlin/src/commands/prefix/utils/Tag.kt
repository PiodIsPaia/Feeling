package com.github.feeling.src.commands.prefix.utils

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class Tag : ListenerAdapter() {
    private val bot = Bot()
    private val db = Database.instance

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val content = event.message.contentRaw
        val prefix = getPrefix(event.guild) ?: bot.prefix
        val guildId = event.guild.id

        val tagDocument = getTagDocument(guildId)
        val tagsArray = getTagsArray(tagDocument)

        tagsArray?.forEach { tag ->
            val tagName = tag.getString("name")
            val trigger = "${prefix}tag $tagName"

            if (content.startsWith(trigger, ignoreCase = true)) {
                val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

                if (prefixCommandsActive) {
                    val response = tag.getString("response")
                    event.channel.sendMessage(response).queue()
                    return@forEach
                } else {
                    return
                }
            }
        }
    }

    private fun getTagDocument(guildId: String): Document? {
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, "tags")
        return collection?.find(Document("guildId", guildId))!!.firstOrNull()
    }

    private fun getTagsArray(tagDocument: Document?): List<Document>? {
        return if (tagDocument != null && tagDocument.contains("tags")) {
            val tags = tagDocument["tags"]
            if (tags is List<*>) {
                tags.filterIsInstance<Document>()
            } else {
                null
            }
        } else {
            null
        }
    }
}
