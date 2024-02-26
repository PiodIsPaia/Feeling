package com.github.feeling.src.commands.prefix

import com.github.feeling.src.commands.prefix.bot.Help
import com.github.feeling.src.commands.prefix.squarecloud.Apps
import com.github.feeling.src.commands.prefix.economy.Daily
import com.github.feeling.src.commands.prefix.economy.Wallet
import com.github.feeling.src.commands.prefix.`fun`.Hello
import com.github.feeling.src.commands.prefix.`fun`.Hug
import com.github.feeling.src.commands.prefix.`fun`.Kiss
import com.github.feeling.src.commands.prefix.squarecloud.Status
import com.github.feeling.src.commands.prefix.utils.Ping
import com.github.feeling.src.commands.prefix.utils.VirusTotal
import com.github.feeling.src.config.Config
import com.github.feeling.src.database.schema.Guild
import com.github.feeling.src.database.utils.arePrefixCommandsActive
import com.github.feeling.src.database.utils.getPrefix
import com.github.feeling.src.database.utils.getTagDocument
import com.github.feeling.src.database.utils.getTagsArray
import com.github.feeling.src.modules.ia.ActivateIA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class PrefixCommands : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val content = event.message.contentRaw
        val config = Config()
        val prefix = getPrefix(Guild(event.guild.id, event.guild.name)) ?: config.prefix

        val botMention = "<@${event.jda.selfUser.id}>"

        val guildId = event.guild.id
        val tagDocument = getTagDocument(guildId)
        val tagsArray = getTagsArray(tagDocument)

        when {
            content.startsWith("$botMention prefix commands on") -> {
                EnablePrefixCommands().execute(event, true)
            }
            content.startsWith("$botMention prefix commands off") -> {
                EnablePrefixCommands().execute(event, false)
            }
            content.startsWith("$botMention enable ia") -> {
                ActivateIA().enableModule(event)
            }
            content.startsWith("$botMention disable ia") -> {
                ActivateIA().disableModule(event)
            }
        }

        val prefixCommandsActive = arePrefixCommandsActive(event.guild.id)

        if (!prefixCommandsActive) return

        when {
            content.startsWith(prefix + "ping") -> {
                Ping().execute(event)
            }
            content.startsWith(prefix + "vt") -> {
                VirusTotal().execute(event)
            }
            content.startsWith(prefix + "ajuda") -> {
                Help().execute(event)
            }
            content.startsWith(prefix + "square apps") -> {
                Apps().execute(event)
            }
            content.startsWith(prefix + "square status") -> {
                Status().execute(event)
            }
            content.startsWith(prefix + "daily") -> {
                Daily().execute(event)
            }
            content.startsWith(prefix + "saldo") -> {
                Wallet().execute(event)
            }
            content.startsWith(prefix + "hello") -> {
                Hello.GreetingResponder(event).execute()
            }
            content.startsWith(prefix + "hug") -> {
                Hug().execute(event)
            }
            content.startsWith(prefix + "kiss") -> {
                Kiss().execute(event)
            }
            tagsArray != null -> {
                tagsArray.forEach { tag ->
                    val tagName = tag.getString("name")
                    val trigger = "${prefix}tag $tagName"

                    if (content.startsWith(trigger, ignoreCase = true)) {

                        val response = tag.getString("response")
                        event.channel.sendMessage(response).queue()

                        return@forEach
                    }
                }
            }
        }
    }
}