package com.github.feeling.src.core

import com.github.feeling.src.commands.slash.registerSlashCommands
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.requests.GatewayIntent

class Client() {
    fun run(token: String) {
        val index = Index()
        val jda = JDABuilder.createDefault(token)
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))

        index.registerCommands("com.github.feeling.src.commands.slash", jda, "Slash Command: ")
        index.registerGlobal("com.github.feeling.src.commands.prefix", jda, "Prefix Command: ")
        index.registerGlobal("com.github.feeling.src.components", jda, "Component: ")
        index.registerGlobal("com.github.feeling.src.events", jda, "Event: ")

        val updatedJDA = jda.build()

        updatedJDA.awaitReady()
        registerSlashCommands(updatedJDA, "com.github.feeling.src.commands.slash")
    }
}