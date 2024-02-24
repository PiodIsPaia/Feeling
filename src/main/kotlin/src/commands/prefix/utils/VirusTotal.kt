package com.github.feeling.src.commands.prefix.utils

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.getPrefix
import com.github.feeling.src.systens.VirusTotalManager
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.bson.Document
import org.json.JSONObject
import java.awt.Color
import java.io.File

class VirusTotal : ListenerAdapter() {
    private val config = Config()
    private val apiKey = dotenv().get("VT_KEY")
    private val virusTotal = VirusTotalManager(apiKey)
    private val db = Database.instance

    companion object {
        var scannedFile: File? = null
        var analysisInfo: String? = null
        lateinit var embed: MessageEmbed
        lateinit var button: Button
    }

    fun execute(event: MessageReceivedEvent) {

        if (!isVIP(event.author.id)) return
        handleVirusTotalCommand(event)

    }

    private fun handleVirusTotalCommand(event: MessageReceivedEvent) {
        val attachment = event.message.attachments.firstOrNull()
        if (attachment != null) {
            handleAttachment(event, attachment)
        } else {
            event.channel.sendMessage("Nenhum arquivo foi enviado.").queue()
        }
    }

    private fun handleAttachment(event: MessageReceivedEvent, attachment: Message.Attachment) {
        val loading = config.getEmoji("loading")
        val message = event.message.reply("$loading Verificando o arquivo, aguarde uns segundos!").complete()

        val file = downloadAttachment(attachment)
        scannedFile = file
        val scanResult = virusTotal.scanFile(file)
        val jsonObject = JSONObject(scanResult)
        val analysisLink = jsonObject.getJSONObject("data").getJSONObject("links").getString("self")
        analysisInfo = virusTotal.getFileAnalysis(analysisLink)

        val msg = """
            ## Análise do VirusTotal
            
            > **Arquivo:**
            > ``${file.name}``
            > **SHA-256:**
            > ``${virusTotal.calculateHash(file)}``
            ### Clique no botão abaixo para ver os resultados dos antivírus.
        """.trimIndent()

        embed = EmbedBuilder()
            .setDescription(msg)
            .setColor(Color.decode(config.colorEmbed))
            .build()

        button = Button.secondary("view_results", "Resultados")
        message.editMessageEmbeds(embed).setContent(null).setActionRow(button).queue {
            file.delete()
        }
    }

    private fun downloadAttachment(attachment: Message.Attachment): File {
        val fileName = attachment.fileName
        val file = File(fileName)
        attachment.downloadToFile(file).join()
        return file
    }

    fun parseScanResults(report: String): MessageEmbed {
        val jsonObject = JSONObject(report)

        val embedBuilder = EmbedBuilder()

        val scanResults = jsonObject.getJSONObject("data").getJSONObject("attributes").getJSONObject("last_analysis_results")
        val antivirusInfo = StringBuilder()
        var isMalicious = false

        scanResults.keys().forEach { antivirusName ->
            val antivirusResult = scanResults.getJSONObject(antivirusName)

            if (!antivirusResult.isNull("result")) {
                val result = antivirusResult.getString("result")
                val category = antivirusResult.getString("category")
                if (category != "undetected") {
                    antivirusInfo.append("**$antivirusName:** ")
                    if (category == "malicious") {
                        antivirusInfo.append("`$result`")
                    } else {
                        antivirusInfo.append(result)
                    }
                    antivirusInfo.append("\n")
                    isMalicious = true
                }
            }
        }

        if (isMalicious) {
            embedBuilder.addField("**Resultados**", antivirusInfo.toString(), true)
            embedBuilder.setColor(Color.RED)
            embedBuilder.setFooter("Este arquivo é potencialmente malicioso!")
        } else {
            embedBuilder.addField("**Resultado**", "``O arquivo é seguro.``", true)
            embedBuilder.setColor(Color.GREEN)
            embedBuilder.setFooter("Nenhum antivírus detectou alguma ameaça no seu arquivo")
        }

        return embedBuilder.build()
    }

    private fun isVIP(userId: String): Boolean {
        val database = db.client?.getDatabase("Feeling")
        val collection = getOrCreateCollection(database, "users_premium")

        val filter = Document("user_id", userId)
        val result = collection?.find(filter)?.firstOrNull()

        return result != null
    }
}
