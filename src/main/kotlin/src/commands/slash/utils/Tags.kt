package com.github.feeling.src.commands.slash.utils

import com.github.feeling.src.commands.slash.Option
import com.github.feeling.src.commands.slash.SlashCommandData
import com.github.feeling.src.commands.slash.SubCommand
import com.github.feeling.src.database.Database
import com.github.feeling.src.database.utils.getOrCreateCollection
import com.github.feeling.src.database.utils.TagManager
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

class Tags : SlashCommandData(
    name = "tag",
    description = "[ Tags ] Configure minhas Tags.",
    permission = arrayOf(Permission.ADMINISTRATOR),
    subcommands = listOf(
        SubCommand(
            name = "criar",
            description = "[ Tags ] Crie tags para para seu servidor para facilitar na hora de responder dúvidas dos usuarios.",
            options = listOf(
                Option(
                    type = OptionType.STRING,
                    name = "nome",
                    description = "Qual será o nome de sua tag? Exemplo de uso caso a tag se chame 'ajuda': f?ajuda.",
                    required = true
                ),
                Option(
                    type = OptionType.STRING,
                    name = "resposta",
                    description = "Quando você fizer o uso da tag qual será a resposta que enviarei para o usuario?",
                    required = true
                )
            )
        ),
        SubCommand(
            name = "ver",
            description = "[ Tags ] Veja minhas tags criadas para este servidor."
        ),
        SubCommand(
            name = "excluir",
            description = "[ Tags ] Exclua algum de minhas tags.",
            options = listOf(
                Option(
                    type = OptionType.STRING,
                    name = "nome",
                    description = "Qual o nome da tag? Caso não saiba use: /tag ver e veja todas as tags criadas.",
                    required = true
                )
            )
        )
    ),
    run = { event ->
        when (event.subcommandName) {
            "criar" -> HandleTag.create(event)
            "ver" -> HandleTag.view(event)
            "excluir" -> HandleTag.remove(event)
        }
    }
)

object HandleTag {
    private val db = Database.instance
    private const val MAXTAGCOUNT = 5

    fun create(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(false).queue()

        val guildId = event.guild!!.id
        val database = db.client?.getDatabase(db.databaseName)
        val collection = getOrCreateCollection(database, "tags")

        val tagManager = TagManager(collection!!, guildId)

        if (tagManager.getTagCount() >= MAXTAGCOUNT) {
            event.hook.editOriginal("Você já atingiu o limite máximo de tags para este servidor (**$MAXTAGCOUNT**)").queue()
            return
        }

        val name = event.getOption("nome")!!.asString
        val response = event.getOption("resposta")!!.asString

        if (name.length > 20 || response.length > 2000) {
            event.hook.editOriginal("O nome da tag deve ter no máximo 20 caracteres e a resposta no máximo 2000 caracteres.").queue()
            return
        }

        tagManager.createTag(name, response)

        val tagNumber = tagManager.getTagCount()
        event.hook.editOriginal("Tag **$tagNumber** criada com sucesso!\n\n**Nome da Tag:** ``$name``\n**Resposta:** ``$response``").queue()
    }

    fun view(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(false).queue()

        val guildId = event.guild!!.id
        val database = db.client?.getDatabase(db.databaseName)
        val collection = getOrCreateCollection(database, "tags")

        val tagManager = TagManager(collection!!, guildId)
        val tagsInfo = tagManager.viewTags()

        event.hook.editOriginal(tagsInfo).queue()
    }
    fun remove(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(false).queue()

        val guildId = event.guild!!.id
        val database = db.client?.getDatabase(db.databaseName)
        val collection = getOrCreateCollection(database, "tags")

        val tagName = event.getOption("nome")?.asString
        if (tagName.isNullOrBlank()) {
            event.hook.editOriginal("Por favor, forneça o nome da tag que deseja excluir.").queue()
            return
        }

        val tagManager = TagManager(collection!!, guildId)

        if (!tagManager.removeTag(tagName)) {
            event.hook.editOriginal("A tag \"$tagName\" não foi encontrada.").queue()
            return
        }

        event.hook.editOriginal("A tag \"$tagName\" foi removida com sucesso.").queue()
    }
}