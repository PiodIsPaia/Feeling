package com.github.feeling.src.database.utils

import com.github.feeling.src.database.Database
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.set
import org.bson.Document

class TagManager(private val collection: MongoCollection<Document>, private val guildId: String) {
    fun getTagCount(): Int {
        val tagDocument = collection.find(Document("guildId", guildId)).firstOrNull()
        val tagsList = tagDocument?.getList("tags", Document::class.java)
        return tagsList?.size ?: 0
    }

    fun createTag(name: String, response: String) {
        val tagDocument = collection.find(Document("guildId", guildId)).firstOrNull()
        val tagsList = tagDocument?.getList("tags", Document::class.java)?.toMutableList() ?: mutableListOf()
        val newTag = Document("name", name).append("response", response)

        tagsList.add(newTag)
        collection.updateOne(Document("guildId", guildId), set("tags", tagsList), UpdateOptions().upsert(true))
    }

    fun viewTags(): String {
        val tagDocument = collection.find(Document("guildId", guildId)).firstOrNull()
        val tagsList = tagDocument?.getList("tags", Document::class.java)

        if (tagsList.isNullOrEmpty()) {
            return "Nenhuma tag foi criada neste servidor."
        }

        val tagInfo = StringBuilder("**Tags Criadas:**\n\n")
        tagsList.forEachIndexed { index, tag ->
            val name = tag.getString("name")
            val response = tag.getString("response")
            val displayIndex = index + 1
            tagInfo.append("**$displayIndex.** ``$name`` - ``$response``\n")
        }

        return tagInfo.toString()
    }

    fun removeTag(tagName: String): Boolean {
        val tagDocument = collection.find(Document("guildId", guildId)).firstOrNull()
        val tagsList = tagDocument?.getList("tags", Document::class.java)

        val tagToRemove = tagsList?.find { it.getString("name") == tagName }
        if (tagToRemove != null) {
            tagsList.remove(tagToRemove)
            collection.updateOne(Document("guildId", guildId), set("tags", tagsList), UpdateOptions().upsert(true))
            return true
        }
        return false
    }
}