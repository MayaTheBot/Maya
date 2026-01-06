package net.puffinmay.maya.website.services

import net.puffinmay.common.website.StatsResponse
import net.puffinmay.maya.MayaInstance

object StatsService {
    fun collect(instance: MayaInstance): StatsResponse {
        val serverCount = instance.shardManager.shards.sumOf { it.guilds.size }
        val usersCount = instance.shardManager.shards.sumOf { it.users.size }

        return StatsResponse(
            serverCount = serverCount,
            usersCount = usersCount,
            commandsCount = 10,
            messagesCount = 10
        )
    }
}
