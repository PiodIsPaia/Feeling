package com.github.feeling.src.core

import com.github.feeling.src.commands.slash.SlashCommandData
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.reflections.Reflections
import org.reflections.scanners.Scanners

class Bot {
    fun registerCommands(packageName: String, jda: JDABuilder, type: String) {
        var commandsLoaded = false
        val errorMessages = mutableListOf<String>()

        try {
            val reflections = Reflections(packageName, Scanners.SubTypes)
            val commandClasses = reflections.getSubTypesOf(SlashCommandData::class.java)

            for (commandClass in commandClasses) {
                try {
                    val commandData = commandClass.getDeclaredConstructor().newInstance() as SlashCommandData
                    val listener = object : ListenerAdapter() {
                        override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
                            if (event.name == commandData.name) {
                                commandData.run.invoke(event)
                            }
                        }
                    }
                    jda.addEventListeners(listener)
                    commandsLoaded = true
                } catch (e: Exception) {
                    errorMessages.add("${type}Erro ao registrar comando ${commandClass.simpleName}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            errorMessages.add("${type}Erro ao obter comandos: ${e.message}")
        }

        if (commandsLoaded) {
            println("$type Commands Carregados com sucesso.")
        } else {
            println("$type Nenhum comando encontrado.")
        }

        errorMessages.forEach { errorMessage ->
            println(errorMessage)
        }
    }

    fun registerListener(packageName: String, jda: JDABuilder, type: String) {
        var componentsAndEventsLoaded = false
        val errorMessages = mutableListOf<String>()

        try {
            val reflections = Reflections(packageName, Scanners.SubTypes)
            val listenerClasses = reflections.getSubTypesOf(ListenerAdapter::class.java)

            for (clazz in listenerClasses) {
                try {
                    val instance = clazz.getDeclaredConstructor().newInstance() as ListenerAdapter
                    jda.addEventListeners(instance)
                    componentsAndEventsLoaded = true
                } catch (e: Exception) {
                    errorMessages.add("${type}Erro ao registrar ${clazz.simpleName}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            errorMessages.add("${type}Erro ao obter components e events: ${e.message}")
        }

        if (componentsAndEventsLoaded) {
            println("$type Carregados com sucesso.")
        } else {
            println("$type Não achei nenhum pacote para registrar.")
        }

        errorMessages.forEach { errorMessage ->
            println(errorMessage)
        }
    }
}