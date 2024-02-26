package com.github.feeling.src.database.utils

import com.github.feeling.src.database.Database
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.set
import org.bson.Document

class TagManager(private val collection: MongoCollection<Document>, private val guildId: String) {
    fun getTagCount(): Int {
        val guildDocument = collection.find(Filters.eq("guild_id", guildId)).firstOrNull()
        val tags = guildDocument?.getList("tags", Document::class.java) ?: emptyList()
        return tags.size
    }

    fun createTag(name: String, response: String) {
        val newTag = Document("name", name).append("response", response)
        collection.updateOne(
            Filters.eq("guild_id", guildId),
            Updates.addToSet("tags", newTag),
            UpdateOptions().upsert(true)
        )
    }

    fun viewTags(): String {
        val guildDocument = collection.find(Filters.eq("guild_id", guildId)).firstOrNull()
        val tags = guildDocument?.getList("tags", Document::class.java) ?: emptyList()

        if (tags.isEmpty()) {
            return "Nenhuma tag foi criada neste servidor."
        }

        val tagInfo = StringBuilder("**Tags Criadas:**\n\n")
        tags.forEachIndexed { index, tag ->
            val name = tag.getString("name")
            val response = tag.getString("response")
            val displayIndex = index + 1
            tagInfo.append("**$displayIndex.** ``$name`` - ``$response``\n")
        }

        return tagInfo.toString()
    }

    fun removeTag(tagName: String): Boolean {
        val guildDocument = collection.find(Filters.eq("guild_id", guildId)).firstOrNull()
        val tags = guildDocument?.getList("tags", Document::class.java) ?: return false

        val updatedTags = tags.filter { it.getString("name") != tagName }
        if (updatedTags.size == tags.size) {
            return false
        }

        collection.updateOne(
            Filters.eq("guild_id", guildId),
            Updates.set("tags", updatedTags),
            UpdateOptions().upsert(true)
        )
        return true
    }
}
