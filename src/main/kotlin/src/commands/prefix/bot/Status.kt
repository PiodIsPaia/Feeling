package com.github.feeling.src.commands.prefix.bot

import com.github.feeling.src.config.Bot
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.lang.management.ManagementFactory
import java.time.Duration

class Status : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val content = event.message.contentRaw

        val prefix = getPrefix(event.guild) ?: Bot().prefix

        if (content.startsWith(prefix + "status")) {
            handleStatusBot(event)
        }
    }

    private fun handleStatusBot(event: MessageReceivedEvent) {
        val uptime = getUptime()
        val ping = getPing(event.jda)
        val ramUsage = getRAMUsage()
        val cpuUsage = getCPUUsage()

        val b = Bot()

        // Emojis
        val loading = b.getEmoji("loading")
        val network = b.getEmoji("network")
        val ram = b.getEmoji("ram")
        val cpu = b.getEmoji("cpu")
        val review = b.getEmoji("review")

        val message = """
            ## $review Machine Info
            - $loading **UpTime:** ${uptime.toHoursPart()}h ${uptime.toMinutesPart()}m ${uptime.toSecondsPart()}s
            - $network **Ping:** $ping ms
            - $ram **Ram:** $ramUsage MB
            - $cpu **CPU:** $cpuUsage%
        """.trimIndent()

        val embed = EmbedBuilder()
            .setDescription(message)
            .setColor(Color.decode(Bot().colorEmbed))
            .build()

        event.message.replyEmbeds(embed).queue()
    }

    private fun getUptime(): Duration {
        val uptimeMillis = ManagementFactory.getRuntimeMXBean().uptime
        return Duration.ofMillis(uptimeMillis)
    }

    private fun getPing(jda: JDA): Long {
        return jda.gatewayPing
    }

    private fun getRAMUsage(): Long {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        return usedMemory / (1024 * 1024)
    }

    private fun getCPUUsage(): String {
        val operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean() as com.sun.management.OperatingSystemMXBean
        val cpuUsage = operatingSystemMXBean.processCpuLoad * 100
        val cpuUsageString = String.format("%.3f", cpuUsage)
        return cpuUsageString.substring(0, minOf(3, cpuUsageString.length))
    }
}