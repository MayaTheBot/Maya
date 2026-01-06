package net.puffinmay.maya.interactions.vanilla.common

import net.puffinmay.maya.interactions.commands.CommandContext
import net.puffinmay.maya.interactions.commands.UnleashedCommandExecutor

class PingExecutor : UnleashedCommandExecutor() {
    override suspend fun execute(context: CommandContext) {
        val gatewayPing = context.jda.gatewayPing
        val currentShardId = context.jda.shardInfo.shardId
        val totalShards = context.jda.shardInfo.shardTotal
        val currentClusterId = context.maya.currentCluster.id
        val currentClusterName = context.maya.currentCluster.name

        context.reply {
            content =
                context.locale["ping.response", currentShardId.toString(), gatewayPing.toString(), currentShardId.toString(), totalShards.toString(), currentClusterId.toString(), currentClusterName]
        }
    }
}