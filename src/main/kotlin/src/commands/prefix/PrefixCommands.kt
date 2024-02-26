package com.github.feeling.src.commands.prefix

import com.github.feeling.src.config.Config
import com.github.feeling.src.database.schema.Guild
import com.github.feeling.src.database.utils.getPrefix
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Modifier

object PrefixCommandRegistry {
    private val commandActions: MutableMap<String, (MessageReceivedEvent) -> Unit> = mutableMapOf()

    fun registerCommands(jda: JDA, commandPackage: String) {
        try {
            val reflections = Reflections(
                ConfigurationBuilder()
                    .forPackages(commandPackage)
                    .addScanners(Scanners.SubTypes)
            )

            val commandClasses = reflections.getSubTypesOf(PrefixCommandBuilder::class.java)

            for (commandClass in commandClasses) {
                if (!Modifier.isAbstract(commandClass.modifiers)) {
                    val commandInstance = commandClass.getDeclaredConstructor().newInstance() as PrefixCommandBuilder

                    commandActions[commandInstance.name.lowercase()] = commandInstance.action
                    commandInstance.aliases.forEach { alias ->
                        commandActions[alias.lowercase()] = commandInstance.action
                    }
                }
            }

            jda.addEventListener(CommandListener())
            println("Comandos por prefixo carregados com sucesso.")
        } catch (e: Exception) {
            println("Erro ao carregar comandos por prefixo: ${e.message}")
            e.printStackTrace()
        }
    }

    fun handleCommand(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        val content = event.message.contentRaw
        val prefix = getPrefix(Guild(event.guild.id, event.guild.name)) ?: Config().prefix

        if (content.startsWith(prefix)) {
            val args = content.substring(prefix.length).split("\\s+".toRegex())
            val commandName = args[0].lowercase()
            val action = commandActions[commandName] ?: return
            action.invoke(event)
        }
    }
}

class CommandListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        PrefixCommandRegistry.handleCommand(event)
    }
}

interface PrefixCommandBuilder {
    val name: String
    val action: (MessageReceivedEvent) -> Unit
    val aliases: Array<String>
}