package com.github.feeling.src.commands.slash.utils

import com.github.feeling.src.commands.slash.SlashCommandData

class Ping : SlashCommandData(
    name = "ping",
    description = "Veja meu ping",
    run = {interaction ->
        interaction.reply("Pong 🏓").setEphemeral(true).queue()
    }
)
