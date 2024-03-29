package com.github.feeling.src.core

import com.github.feeling.src.commands.prefix.PrefixCommandRegistry
import com.github.feeling.src.commands.slash.registerSlashCommands
import com.github.feeling.src.components.RegisterComponents
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import okhttp3.OkHttpClient

class Client {
    fun run(token: String) {
        val bot = Bot()
        val jda = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
            .setHttpClient(OkHttpClient())
            //.addEventListeners(AddPremium())

        bot.registerCommands("com.github.feeling.src.commands.slash", jda, "Slash Command: ")
        bot.registerListener("com.github.feeling.src.events", jda, "Event: ")
        bot.registerListener("com.github.feeling.src.modules", jda, "Module: ")

        jda.setStatus(OnlineStatus.DO_NOT_DISTURB)
        jda.setActivity(Activity.listening("Starting..."))

        val updatedJDA = jda.build()

        updatedJDA.awaitReady()

        val guildCount = updatedJDA.guilds.size

        val status1 = Activity.customStatus("Cuidando de $guildCount servidores")
        val status2 = Activity.listening("Precisando de ajuda? Use /ajuda")
        val status3 = Activity.playing("Servidor de suporte: Em breve...")

        Thread {
            while (true) {
                updatedJDA.presence.activity = status1
                Thread.sleep(60000)

                updatedJDA.presence.activity = status2
                Thread.sleep(60000)

                updatedJDA.presence.activity = status3
                Thread.sleep(60000)
            }
        }.start()

        val buttonPackage = "com.github.feeling.src.components.buttons"
        val selectMenuPackage = "com.github.feeling.src.components.selectMenu"
        val prefixPackage = "com.github.feeling.src.commands.prefix"

        RegisterComponents.registerComponents(updatedJDA, buttonPackage, selectMenuPackage )
        PrefixCommandRegistry.registerCommands(updatedJDA, prefixPackage)
        registerSlashCommands(updatedJDA, "com.github.feeling.src.commands.slash")
    }
}