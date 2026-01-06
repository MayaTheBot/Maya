package net.puffinmay.maya.database.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object Messages : LongIdTable("messages") {
    val channelId = varchar("channel_id", 50)
    val authorId = long("author_id")
    val content = text("content") // json stored as text
    val connection = varchar("connection", 255)
    val reference = varchar("reference", 255).nullable()
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
}

object MessageLikes : UUIDTable("message_likes") {
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val message = reference("message_id", Messages, onDelete = ReferenceOption.CASCADE)
}

object SavedMessages : UUIDTable("saved_messages") {
    val savedAt = long("saved_at").clientDefault { System.currentTimeMillis() }
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val message = reference("message_id", Messages, onDelete = ReferenceOption.CASCADE)
}

object ForwardMessages : UUIDTable("forward_messages") {
    val originMessageId = varchar("origin_message_id", 255)
    val originChannelId = varchar("origin_channel_id", 255)
    val forwardedMessageId = varchar("forwarded_message_id", 255)
    val forwardedChannelId = varchar("forwarded_channel_id", 255)
    val connectionName = varchar("connection_name", 255)
    val originalAuthorId = varchar("original_author_id", 255).nullable()
    val webhookId = varchar("webhook_id", 255).nullable()
    val webhookToken = varchar("webhook_token", 255).nullable()
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
}