package net.puffinmay.maya.database.dao

import net.puffinmay.maya.database.table.ForwardMessages
import net.puffinmay.maya.database.table.MessageLikes
import net.puffinmay.maya.database.table.Messages
import net.puffinmay.maya.database.table.SavedMessages
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class MessagesEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MessagesEntity>(Messages)

    var channelId by Messages.channelId
    var authorId by Messages.authorId
    var content by Messages.content
    var connection by Messages.connection
    var reference by Messages.reference
    var createdAt by Messages.createdAt

    val likes by MessageLike referrersOn MessageLikes.message
    val savedBy by SavedMessage referrersOn SavedMessages.message
}

class MessageLike(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MessageLike>(MessageLikes)

    var createdAt by MessageLikes.createdAt
    var user by UsersEntity referencedOn MessageLikes.user
    var message by MessagesEntity referencedOn MessageLikes.message
}

class SavedMessage(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SavedMessage>(SavedMessages)

    var savedAt by SavedMessages.savedAt
    var user by UsersEntity referencedOn SavedMessages.user
    var message by MessagesEntity referencedOn SavedMessages.message
}

class ForwardMessage(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ForwardMessage>(ForwardMessages)

    var originMessageId by ForwardMessages.originMessageId
    var originChannelId by ForwardMessages.originChannelId
    var forwardedMessageId by ForwardMessages.forwardedMessageId
    var forwardedChannelId by ForwardMessages.forwardedChannelId
    var connectionName by ForwardMessages.connectionName
    var originalAuthorId by ForwardMessages.originalAuthorId
    var webhookId by ForwardMessages.webhookId
    var webhookToken by ForwardMessages.webhookToken
    var createdAt by ForwardMessages.createdAt
}