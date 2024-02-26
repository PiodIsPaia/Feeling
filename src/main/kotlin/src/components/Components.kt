package com.github.feeling.src.components

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Modifier

object RegisterComponents {
    private val buttonActions: MutableMap<String, (ButtonInteractionEvent) -> Unit> = mutableMapOf()
    private val selectMenuActions: MutableMap<String, (StringSelectInteractionEvent) -> Unit> = mutableMapOf()

    fun registerComponents(jda: JDA, buttonPackage: String, selectMenuPackage: String) {
        registerButtons(jda, buttonPackage)
        registerStringSelectMenus(jda, selectMenuPackage)
    }

    private fun registerButtons(jda: JDA, buttonPackage: String) {
        try {
            val reflections = Reflections(
                ConfigurationBuilder()
                    .forPackages(buttonPackage)
                    .addScanners(Scanners.SubTypes)
            )

            val buttonClasses = reflections.getSubTypesOf(Button::class.java)

            for (buttonClass in buttonClasses) {
                if (!Modifier.isAbstract(buttonClass.modifiers)) {
                    val buttonInstance = buttonClass.getDeclaredConstructor().newInstance() as Button

                    buttonActions[buttonInstance.id] = buttonInstance.action
                }
            }

            jda.addEventListener(ButtonListener())
            println("Botões carregados com sucesso.")
        } catch (e: Exception) {
            println("Erro ao carregar botões: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun registerStringSelectMenus(jda: JDA, selectMenuPackage: String) {
        try {
            val reflections = Reflections(
                ConfigurationBuilder()
                    .forPackages(selectMenuPackage)
                    .addScanners(Scanners.SubTypes)
            )

            val selectMenuClasses = reflections.getSubTypesOf(StringSelectMenu::class.java)

            for (selectMenuClass in selectMenuClasses) {
                if (!Modifier.isAbstract(selectMenuClass.modifiers)) {
                    val selectMenuInstance = selectMenuClass.getDeclaredConstructor().newInstance() as StringSelectMenu

                    selectMenuActions[selectMenuInstance.id] = selectMenuInstance.action
                }
            }

            jda.addEventListener(SelectMenuListener())
            println("Menus carregados com sucesso.")
        } catch (e: Exception) {
            println("Erro ao carregar menus: ${e.message}")
            e.printStackTrace()
        }
    }

    fun handleButtonClick(event: ButtonInteractionEvent) {
        val id = event.componentId
        val action = buttonActions[id] ?: return
        action.invoke(event)
    }

    fun handleSelectMenuInteraction(event: StringSelectInteractionEvent) {
        val id = event.componentId
        val action = selectMenuActions[id] ?: return
        action.invoke(event)
    }
}

class ButtonListener : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        RegisterComponents.handleButtonClick(event)
    }
}

class SelectMenuListener : ListenerAdapter() {
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        RegisterComponents.handleSelectMenuInteraction(event)
    }
}

interface Button {
    val id: String
    val action: (ButtonInteractionEvent) -> Unit
}

interface StringSelectMenu {
    val id: String
    val action: (StringSelectInteractionEvent) -> Unit
        get() = {}
}
