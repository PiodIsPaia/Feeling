package com.github.feeling.src.modules.`fun`

import com.github.feeling.src.commands.prefix.admin.AddPremium
import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import com.github.feeling.src.systens.ChatGpt
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.MessageType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document

class IAChat : ListenerAdapter() {
    private val db = Database.instance

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val guildId = event.guild.id
        val isActive = isActive(guildId)
        val botMention = event.jda.selfUser.asMention
        val content = event.message.contentRaw
        val prefix = getPrefix(event.guild) ?: Bot().prefix

        if (isActive) {
            if (!isVIP(event.author.id)) return
            if (content !in listOf("$botMention enable ia", "$botMention disable ia") && !content.startsWith("${prefix}add premium")) {
                when {
                    event.message.mentions.mentionsEveryone() || event.message.mentions.users.contains(event.jda.selfUser) -> processMessage(event)
                    event.isFromGuild && event.message.type == MessageType.DEFAULT && event.message.referencedMessage != null && event.message.referencedMessage?.author == event.jda.selfUser -> processMessage(event)
                }
            }
        }
    }

    private fun processMessage(event: MessageReceivedEvent) {
        event.channel.sendTyping().queue()

        CoroutineScope(Dispatchers.Default).launch {
            val apiKey = dotenv()["OPENAI_KEY"]
            val openAi = ChatGpt(apiKey)

            val system = "Você é a Feeling, uma personagem fictícia com uma personalidade feminina, dedicada a esclarecer dúvidas, oferecer assistência e adicionar uma dose de humor ao bate-papo com respostas engraçadas. Você serve para ser sua companhia virtual, pronta para tornar suas interações mais divertidas e informativas. E não dê respostas com mais de 100 palavras! Caso alguém mencione o nome \"Daniel\", apenas responda com: é but "

            val message = openAi.askQuestion(context = system, question =  event.message.contentRaw)
            event.message.reply(message).queue()
        }
    }

    private fun isActive(guildId: String): Boolean {
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, "modules")

        val filter = Document("guild_id", guildId)
        val result = collection?.find(filter)?.firstOrNull()

        return result?.getBoolean("conversationEnabled") ?: false
    }

    private fun isVIP(userId: String): Boolean {
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, "users_premium")

        val filter = Document("user_id", userId)
        val result = collection?.find(filter)?.firstOrNull()

        return result != null
    }
}