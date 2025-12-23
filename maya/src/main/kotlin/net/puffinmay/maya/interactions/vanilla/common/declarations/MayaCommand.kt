package net.puffinmay.maya.interactions.vanilla.common.declarations

import net.puffinmay.maya.interactions.commands.CommandDeclarationWrapper
import net.puffinmay.maya.interactions.vanilla.common.PingExecutor
import net.dv8tion.jda.api.interactions.IntegrationType
import net.dv8tion.jda.api.interactions.InteractionContextType

class MayaCommand : CommandDeclarationWrapper {
    override fun create() = slashCommand("maya") {
        interactionContexts = listOf(
            InteractionContextType.BOT_DM,
            InteractionContextType.GUILD,
            InteractionContextType.PRIVATE_CHANNEL
        )

        integrationType = listOf(IntegrationType.USER_INSTALL, IntegrationType.GUILD_INSTALL)

        subCommand("ping") {
            aliases = listOf("ping")
            executor = PingExecutor()
        }
    }
}