package net.puffinmay.maya.interactions.vanilla.connections.declarations

import net.puffinmay.maya.interactions.commands.CommandDeclarationWrapper
import net.puffinmay.maya.interactions.vanilla.connections.ConnectionCreateExecutor
import net.puffinmay.maya.interactions.vanilla.connections.ConnectionInformationExecutor
import net.puffinmay.maya.interactions.vanilla.connections.ConnectionJoinExecutor
import net.dv8tion.jda.api.interactions.IntegrationType
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.OptionType

class ConnectionsCommand : CommandDeclarationWrapper {
    override fun create() = slashCommand("connections") {
        interactionContexts = listOf(InteractionContextType.GUILD)
        integrationType = listOf(IntegrationType.GUILD_INSTALL)

        subCommand("create") {
            executor = ConnectionCreateExecutor()
        }

        subCommand("join") {
            addOption(opt(OptionType.STRING, "connection", true), isSubCommand = true)
            addOption(opt(OptionType.CHANNEL, "channel", false), isSubCommand = true)

            executor = ConnectionJoinExecutor()
        }

        subCommand("info") {
            addOption(opt(OptionType.STRING, "connection", false), isSubCommand = true)

            executor = ConnectionInformationExecutor()
        }
    }
}