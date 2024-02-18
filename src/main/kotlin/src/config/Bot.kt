package com.github.feeling.src.config

class Bot {
    val prefix: String = "f?"
    val colorEmbed: String = "#2b2d31"

    private val emojiMap: Map<String, String> = mapOf(
        "loading" to "<a:carregando:1207471259202494544>",
        "bug_hunter" to "<:Sv_BugHunter:1207465094058090576>",
        "toggle_on" to "️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️️<:3350toggleon:1207470330977976390>",
        "toggle_off" to "<:Sv_ToggleOFF:1207470575711420427>",
        "confirm_gif" to "<a:Confirm_gif:1207471312595980350>",
        "network" to "<:network:1207477930851303506>",
        "power_hug" to "<:E_pochipowerhug_madebytayerexx:1208187964568633435>",
        "joikiss_gif" to "<a:joikiss:1208193747523993605>",
        "gura_greeting" to "<:gawrgurawave:1208198970946490408>",
        "dev" to "<:dev:1208784455079039007>",
        "review" to "<:review:1208784861746298912>",
        "cpu" to "<:cpu:1208785454284148798>",
        "ram" to "<:ram:1208785556784549918>"
    )

    fun getEmoji(name: String): String? {
        return emojiMap[name]
    }
}