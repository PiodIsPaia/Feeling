package com.github.feeling.src.commands.prefix.utils

import com.github.feeling.src.commands.prefix.PrefixCommandBuilder
import com.github.feeling.src.config.Config
import com.github.feeling.src.database.schema.Guild
import com.github.feeling.src.database.utils.getPrefix
import com.github.feeling.src.database.utils.getTagDocument
import com.github.feeling.src.database.utils.getTagsArray
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Tags : PrefixCommandBuilder {
    override val name: String = "tag"
    override val aliases: Array<String> = arrayOf("tags")
    override val action: (MessageReceivedEvent) -> Unit = { event ->
        processTagCommand(event)
    }

    private fun processTagCommand(event: MessageReceivedEvent) {
        val content = event.message.contentRaw
        val prefix = getPrefix(Guild(event.guild.id, event.guild.name)) ?: Config().prefix

        val guildId = event.guild.id
        val tagDocument = getTagDocument(guildId)
        val tagsArray = getTagsArray(tagDocument)

        tagsArray?.let { tags ->
            for (tag in tags) {
                val tagName = tag.getString("name")

                val triggers = listOf("$prefix$name $tagName", *aliases.map { "$prefix$it $tagName" }.toTypedArray())

                for (trigger in triggers) {
                    if (content.startsWith(trigger, ignoreCase = true)) {
                        val response = tag.getString("response")
                        event.channel.sendMessage(response).queue()
                        return
                    }
                }
            }
        }
    }
}
