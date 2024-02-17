package com.github.feeling.src.commands.slash.utils

import com.github.feeling.src.commands.slash.SlashCommandData

class Ping : SlashCommandData(
    name = "ping",
    description = "[ Feeling ] Veja meu ping",
    run = {interaction ->
        // Responde a mensagem do usuario com um simples "Pong"
        interaction.reply("Pong 🏓").setEphemeral(true).queue()
    }
)
