package com.github.feeling.src.database.schema

import com.github.feeling.src.config.Config
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty

data class Guild @BsonCreator constructor(
    @BsonProperty("guild_id") val guildId: String,
    @BsonProperty("guild_name") val guildName: String,
    @BsonProperty("premium") val premium: Boolean? = false,
    @BsonProperty("prefix_commands") val prefixCommands: Boolean? = false,
    @BsonProperty("prefix") val prefix: String = Config().prefix,
    @BsonProperty("ia") val ia: Boolean? = false,
    @BsonProperty("games") val games: Boolean? = false,
    @BsonProperty("tags") val tags: List<Tags>? = emptyList()
)

data class Tags @BsonCreator constructor(
    @BsonProperty("name") val name: String?,
    @BsonProperty("response") val response: String?
)
