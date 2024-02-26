package com.github.feeling.src.components.buttons.premium

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.mongodb.client.model.Filters
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bson.Document
import java.awt.Color
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.schedule

class BuyPremium : ListenerAdapter() {
    private val db = Database.instance
    private val config = Config()

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when (event.componentId) {
            "button_buy_premium" -> handlerBuy(event)
        }
    }

    private fun handlerBuy(event: ButtonInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        val userId = event.user.id
        val walletCollection = getOrCreateCollection(db.client?.getDatabase(db.databaseName), "wallet")
        val premiumCollection = getOrCreateCollection(db.client?.getDatabase(db.databaseName), "users_premium")

        val existingPremium = premiumCollection?.find(Filters.eq("user_id", userId))?.firstOrNull()
        if (existingPremium != null) {
            val expirationDate = existingPremium.getDate("expiration_date").toInstant()
            val remainingTime = Duration.between(Instant.now(), expirationDate)
            val remainingDays = remainingTime.toDays()
            val remainingHours = remainingTime.toHours() % 24
            val remainingMinutes = remainingTime.toMinutes() % 60

            val embedDuration = EmbedBuilder()
                .setTitle("Relaxa! Você já possui uma assinatura premium ativa.")
                .setDescription("Tempo restante: **$remainingDays dias, $remainingHours horas e $remainingMinutes minutos.**")
                .setColor(Color.GREEN)
                .setFooter("Lembrando que quando terminar o período você só poderá ser premium novamente usando milhos após uma semana.", "https://i.imgur.com/zNgWl4r.gif")
                .build()

            event.hook.editOriginalEmbeds(embedDuration).queue()
            return
        }

        val walletDocument = walletCollection?.find(Filters.eq("user_id", userId))?.firstOrNull()
        val money = walletDocument?.getInteger("balance") ?: 0
        if (money < 5000) {
            event.hook.editOriginal("Você não tem dinheiro suficiente para comprar a assinatura premium.").queue()
            return
        }

        walletCollection?.updateOne(
            Filters.eq("user_id", userId),
            Document("\$inc", Document("balance", -5000))
        )

        val expirationDate = Instant.now().plus(3, ChronoUnit.DAYS)
        val userDocument = Document("user_id", userId)
            .append("expiration_date", expirationDate)
        premiumCollection?.insertOne(userDocument)

        event.hook.editOriginal("Parabéns! Você adquiriu a assinatura premium com sucesso!").queue()

        val delay = expirationDate.toEpochMilli() - System.currentTimeMillis()
        Timer().schedule(delay) {
            premiumCollection?.deleteOne(userDocument)
        }
    }
}
