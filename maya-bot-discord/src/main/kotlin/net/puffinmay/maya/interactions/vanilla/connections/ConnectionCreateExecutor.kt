package net.puffinmay.maya.interactions.vanilla.connections

import net.puffinmay.maya.interactions.commands.CommandContext
import net.puffinmay.maya.interactions.commands.UnleashedCommandExecutor
import net.puffinmay.maya.interactions.pretty
import net.puffinmay.common.MayaEmotes

class ConnectionCreateExecutor : UnleashedCommandExecutor() {
    override suspend fun execute(context: CommandContext) {
        if (context.guild == null) {
            context.reply(ephemeral = true) {
                content = pretty(
                    MayaEmotes.error,
                    context.locale["generics.error.notInGuild", context.user.asMention]
                )
            }

            return
        }

        context.reply {
            content = pretty(
                MayaEmotes.success,
                context.locale["connections.create.response", context.user.asMention, context.guild?.idLong.toString()]
            )
        }
    }
}