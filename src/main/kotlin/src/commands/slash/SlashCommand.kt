package com.github.feeling.src.commands.slash

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder

open class SlashCommandData(
    val name: String,
    val description: String,
    val run: (event: SlashCommandInteractionEvent) -> Unit,
    val isOnlyGuild: Boolean = true,
    val permission: Array<Permission> = arrayOf(Permission.MESSAGE_SEND),
    val options: List<Option> = emptyList(),
    val subcommands: List<SubCommand> = emptyList(),
    val subcommandsGroup: List<SubCommandGroup> = emptyList()
)


data class Option(
    val type: OptionType,
    val name: String,
    val description: String,
    val required: Boolean = false,
    val channelType: ChannelType? = null
)

data class SubCommand(
    val name: String,
    val description: String,
    val options: List<Option> = emptyList(),
)

data class SubCommandGroup(
    val name: String,
    val description: String,
    val subcommands: List<SubCommand>
)

fun registerSlashCommands(jda: JDA, commandPackage: String) {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages(commandPackage)
            .addScanners(Scanners.SubTypes)
    )

    val commandClasses = reflections.getSubTypesOf(SlashCommandData::class.java)

    val commandListUpdateAction: CommandListUpdateAction = jda.updateCommands()

    for (commandClass in commandClasses) {
        try {
            val commandData = commandClass.getDeclaredConstructor().newInstance() as SlashCommandData

            val slashCommandData = Commands.slash(commandData.name, commandData.description)

            for (option in commandData.options) {
                val optionData = OptionData(option.type, option.name, option.description)
                    .setRequired(option.required)
                if (option.channelType != null) {
                    optionData.setChannelTypes(option.channelType)
                }

                slashCommandData.addOptions(optionData)
            }

            for (subcommand in commandData.subcommands) {
                val subcommandData = SubcommandData(subcommand.name, subcommand.description)

                for (subcommandOption in subcommand.options) {
                    val subcommandOptionData = OptionData(subcommandOption.type, subcommandOption.name, subcommandOption.description)
                        .setRequired(subcommandOption.required)

                    subcommandData.addOptions(subcommandOptionData)
                }

                slashCommandData.addSubcommands(subcommandData)
            }

            for (subcommandGroup in commandData.subcommandsGroup) {
                val subcommandGroupData = SubcommandGroupData(subcommandGroup.name, subcommandGroup.description)

                for (subcommand in subcommandGroup.subcommands) {
                    val subcommandData = SubcommandData(subcommand.name, subcommand.description)

                    for (subcommandOption in subcommand.options) {
                        val subcommandOptionData = OptionData(subcommandOption.type, subcommandOption.name, subcommandOption.description)
                            .setRequired(subcommandOption.required)

                        subcommandData.addOptions(subcommandOptionData)
                    }

                    subcommandGroupData.addSubcommands(subcommandData)
                }

                slashCommandData.addSubcommandGroups(subcommandGroupData)
            }

            commandListUpdateAction.addCommands(slashCommandData.setGuildOnly(commandData.isOnlyGuild).setDefaultPermissions(
                DefaultMemberPermissions.enabledFor(*commandData.permission)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    commandListUpdateAction.queue()
}