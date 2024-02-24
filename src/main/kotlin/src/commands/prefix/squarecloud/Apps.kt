package com.github.feeling.src.commands.prefix.squarecloud

import com.github.feeling.src.config.Config
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.awt.Color
import java.util.*

class Apps : ListenerAdapter() {
    fun execute(event: MessageReceivedEvent) {

        val b = Config()

        // Emojis
        //val loading = b.getEmoji("loading")
        val network = b.getEmoji("network")
        val ram = b.getEmoji("ram")
        val review = b.getEmoji("review")
        val square = b.getEmoji("square_cloud")
        val java = b.getEmoji("java")
        val cluster = b.getEmoji("cluster")

        val authorization = dotenv()["SQUAREAPI_KEY"]
        val squareInfo = fetchSquareInfo(authorization)

        val user = squareInfo.getJSONObject("response").getJSONObject("user")
        val plan = user.getJSONObject("plan")
        val applications = squareInfo.getJSONObject("response").getJSONArray("applications")

        val planName = plan.getString("name")
        val memory = plan.getJSONObject("memory")
        val limit = memory.getInt("limit")
        val used = memory.getInt("used")
        val ping = getPing(event)

        val embedMessage = """
            $square Square Info
            ## 💰 Plano: ``${planName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}``
            - $review **Usuario:** ``${user.getString("tag")}``
            - $ram **Memória:** ``$used/${limit}MB``
            - $network **Ping:** ``$ping``ms
            
            ## $square Aplicativos:
        """.trimIndent()

        val embed = EmbedBuilder()
            .setThumbnail("https://media.discordapp.net/attachments/1149134050880127119/1210618474733371393/logo.png?ex=65eb3750&is=65d8c250&hm=72b3ff60f8379fe19631c0dba2314e1c78f694261f286190728af7973134680a&=&format=webp&quality=lossless")
            .setDescription(embedMessage)
            .setColor(Color.decode(Config().colorEmbed))

        for (i in 0 until applications.length()) {
            val app = applications.getJSONObject(i)
            embed.addField("Aplicativo ${i + 1}",
                """
                - $review **Tag:** ``${app.getString("tag")}``
                - $ram **RAM:** ``${app.getInt("ram")}MB``
                - $java **Linguagem:** ``${app.getString("lang")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}``
                - $cluster **Cluster:** ``${app.getString("cluster")}``
                """.trimIndent(), false)
        }

        event.message.replyEmbeds(embed.build()).queue()
    }

    private fun getPing(event: MessageReceivedEvent): Long {
        return event.jda.gatewayPing
    }

    private fun fetchSquareInfo(authorization: String): JSONObject {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.squarecloud.app/v2/user")
            .header("Authorization", authorization)
            .build()

        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            val responseData = response.body.string()
            return JSONObject(responseData)
        } finally {
            response?.close()
        }
    }
}