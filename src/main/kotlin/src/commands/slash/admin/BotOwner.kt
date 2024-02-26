package com.github.feeling.src.commands.slash.admin

import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.commands.slash.SubCommand
import com.github.feeling.src.commands.slash.SubCommandGroup
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.bson.Document
import java.util.*
import kotlin.concurrent.schedule

class BotOwner : SlashCommandData(
    name = "admin",
    description = "[ Exclusivo ] Comandos exclusivo",
    subcommandsGroup = listOf(
        SubCommandGroup(
            name = "premium",
            description = "[ Admin ] Gerenciar premium",
            subcommands = listOf(
                SubCommand(
                    name = "add",
                    description = "[ Admin ] Dar premium para alguém",
                    options = listOf(
                        Option(
                            name = "user",
                            description = "Usuário a receber o premium",
                            type = OptionType.USER,
                            required = true
                        ),
                        Option(
                            name = "duration",
                            description = "Duração do premium",
                            type = OptionType.STRING,
                            required = true
                        ),
                        Option(
                            name = "time_unit",
                            description = "Unidade de tempo (minute, hour, day)",
                            type = OptionType.STRING,
                            required = true
                        )
                    )
                )
            )
        )
    ),
    run = { event ->
        val userOption = event.getOption("user")!!.asUser!!
        val duration = event.getOption("duration")!!.asString.toInt()
        val timeUnit = event.getOption("time_unit")!!.asString

        val handlerAddPremium = AddPremiumSlashHandler()
        handlerAddPremium.execute(event, userOption, duration, timeUnit)
    }
)

class AddPremiumSlashHandler {
    private val db = Database.instance

    fun execute(event: SlashCommandInteractionEvent, userOption: User, duration: Int, timeUnit: String) {
        if (!event.user.isBot) {
            if (event.user.id != dotenv()["BOT_OWNER_ID"]) {
                event.reply("Você não pode executar esta ação!").setEphemeral(true).queue()
                return
            }

            val userId = userOption.id
            val database = db.client?.getDatabase(db.databaseName)
            val collection = getOrCreateCollection(database, "users")

            val existingUser = collection?.find(Document("user_id", userId))?.firstOrNull()
            if (existingUser == null) {
                event.reply("O usuário ${userOption.asMention} não foi encontrado.").setEphemeral(true).queue()
                return
            }

            val premium = existingUser["premium"] as? Document
            if (premium != null && premium.getBoolean("active") == true) {
                event.reply("O usuário ${userOption.asMention} já está no grupo Premium.").setEphemeral(true).queue()
                return
            }

            val expirationTime = calculateExpirationTime(duration, timeUnit)

            val premiumDocument = Document("active", true)
                .append("expiration", expirationTime)

            val update = Updates.set("premium", premiumDocument)
            val filter = Filters.eq("user_id", userId)

            collection.updateOne(filter, update)

            event.reply("Prontinho! Adicionei o ${userOption.asMention} à lista de usuários **Premium**.").queue()

            val delay = expirationTime - System.currentTimeMillis()
            Timer().schedule(delay) {
                val unsetPremium = Updates.unset("premium")
                collection.updateOne(filter, unsetPremium)
                event.channel.sendMessage("O período Premium de ${userOption.asMention} expirou.").queue()
            }
        }
    }

    private fun calculateExpirationTime(duration: Int, timeUnit: String): Long {
        val now = System.currentTimeMillis()
        val millisecondsInMinute = 60000L
        val millisecondsInHour = 3600000L
        val millisecondsInDay = 86400000L

        return when (timeUnit.lowercase()) {
            "minute", "minutes" -> now + duration * millisecondsInMinute
            "hour", "hours" -> now + duration * millisecondsInHour
            "day", "days" -> now + duration * millisecondsInDay
            else -> throw IllegalArgumentException("Unidade de tempo inválida.")
        }
    }
}