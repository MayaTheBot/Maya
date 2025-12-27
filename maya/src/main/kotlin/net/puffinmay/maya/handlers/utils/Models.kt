package net.puffinmay.maya.handlers.utils

import net.dv8tion.jda.api.entities.Message

data class ForwardResult(
    val success: Boolean,
    val reason: String? = null,
    val sentMessage: Message? = null
)

data class SanitizedMessage(
    val id: String,
    val content: String,
    val channelId: String,
    val author: MessageAuthor
)

data class MessageAuthor(
    val id: String,
    val name: String,
    val avatarUrl: String?
)

data class ProcessedAttachment(
    val url: String,
    val filename: String
)

data class DestinationConnection(
    val name: String,
    val messageComponentType: Int,
    val flags: Int = 0
)

data class ReferenceMessage(
    val message: Message,
    val author: ReferenceAuthor,
    val data: ReferenceData
)

data class ReferenceAuthor(
    val userId: String,
    val userName: String,
    val avatarUrl: String?,
    val allowMentions: Boolean
)

data class ReferenceData(
    val originChannelId: String,
    val originGuildId: String,
    val forwardedMessageId: String?
)

data object OrbitConnectionFlags {
    const val Origin = 1 shl 0
}