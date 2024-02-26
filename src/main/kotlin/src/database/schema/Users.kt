package com.github.feeling.src.database.schema

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty


data class Wallet @BsonCreator constructor(
    @BsonProperty("milhos") val milhos: Int,
    @BsonProperty("lastClaimDaily") val lastClaimDaily: Long
)

data class Premium @BsonCreator constructor(
    @BsonProperty("active") val active: Boolean? = false,
    @BsonProperty("expiration") val expiration: Long
)

data class User @BsonCreator constructor(
    @BsonProperty("user_id") val userId: String,
    @BsonProperty("username") val username: String,
    @BsonProperty("wallet") val wallet: Wallet,
    @BsonProperty("premium") val premium: Premium?
)
