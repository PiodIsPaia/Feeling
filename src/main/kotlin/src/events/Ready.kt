package com.github.feeling.src.events

import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Ready : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        println("Entrei como: ${event.jda.selfUser.name}")
    }
}