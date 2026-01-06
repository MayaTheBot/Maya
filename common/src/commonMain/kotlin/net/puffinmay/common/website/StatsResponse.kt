package net.puffinmay.common.website

import kotlinx.serialization.Serializable

@Serializable
data class StatsResponse(
    val serverCount: Int,
    val usersCount: Int,
    val commandsCount: Int,
    val messagesCount: Int,
)