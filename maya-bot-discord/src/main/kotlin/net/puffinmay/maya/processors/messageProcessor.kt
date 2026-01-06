package net.puffinmay.maya.processors

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.puffinmay.maya.database.dao.ConnectionsEntity
import net.puffinmay.maya.database.dao.ForwardMessage
import net.puffinmay.maya.database.dao.MessagesEntity
import net.puffinmay.maya.database.data.SendMessage
import net.puffinmay.maya.database.table.Connections
import net.puffinmay.maya.database.table.ForwardMessages
import net.puffinmay.maya.handlers.ForwardHandler
import net.puffinmay.maya.handlers.utils.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

data object MessageProcessor {
    suspend fun process(message: MessageReceivedEvent, sourceConnection: ConnectionsEntity) {
        MessagesEntity.SendMessage(
            message.messageIdLong,
            message.message.contentRaw,
            message,
            sourceConnection
        )

        val allDestinations = newSuspendedTransaction {
            ConnectionsEntity.find {
                (Connections.name eq sourceConnection.name) and
                        (Connections.channelId neq message.channel.idLong) and
                        (Connections.locked.isNull() or (Connections.locked eq false)) and
                        (Connections.paused.isNull() or (Connections.paused eq false))
            }.toList()
        }

        val seenChannels = mutableSetOf<Long>()
        val uniqueDestinations = allDestinations.filter { conn ->
            if (seenChannels.contains(conn.channelId)) {
                false
            } else {
                seenChannels.add(conn.channelId)
                true
            }
        }

        if (uniqueDestinations.isEmpty()) return

        val originalGuild = message.guild
        val content = message.message.contentRaw

        val processedAttachments = message.message.attachments.associate { attachment ->
            attachment.id to ProcessedAttachment(
                url = attachment.url,
                filename = attachment.fileName
            )
        }

        val reference = message.message.referencedMessage?.let { refMsg ->
            val (authorId, authorName, authorAvatar) = if (refMsg.author.isBot) {
                newSuspendedTransaction {
                    ForwardMessage.find {
                        ForwardMessages.forwardedMessageId eq refMsg.id
                    }.firstOrNull()?.let { forward ->
                        forward.originalAuthorId?.let { originalId ->
                            try {
                                message.jda.getUserById(originalId)?.let {
                                    Triple(it.id, it.name, it.avatarUrl)
                                } ?: Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                            } catch (_: Exception) {
                                Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                            }
                        } ?: Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                    } ?: Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                }
            } else {
                Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
            }

            ReferenceMessage(
                message = refMsg,
                author = ReferenceAuthor(
                    userId = authorId,
                    userName = authorName,
                    avatarUrl = authorAvatar,
                    allowMentions = true
                ),
                data = ReferenceData(
                    originChannelId = message.channel.id,
                    originGuildId = message.guild.id,
                    forwardedMessageId = refMsg.id
                )
            )
        }

        var reactionAdded = false
        try {
            message.message.addReaction(Emoji.fromUnicode("☄️")).queue()
            reactionAdded = true
        } catch (_: Exception) {
        }

        val size = 10

        uniqueDestinations
            .chunked(size)
            .forEach { batch ->
                coroutineScope {
                    batch.map { dest ->
                        async {
                            try {
                                val targetChannel = message.jda.getTextChannelById(dest.channelId)
                                if (targetChannel == null) {
                                    println("Channel ${dest.channelId} not found")
                                    return@async
                                }

                                val destinationConnection = DestinationConnection(
                                    name = dest.name,
                                    messageComponentType = dest.messageComponentType,
                                    flags = dest.flags
                                )

                                ForwardHandler.forward(
                                    destinationConnection = destinationConnection,
                                    sourceMessage = message.message,
                                    sourceGuild = originalGuild,
                                    targetChannel = targetChannel,
                                    processedAttachments = processedAttachments,
                                    content = content,
                                    reference = reference,
                                    isEdit = false
                                )
                            } catch (_: Exception) {
                            }
                        }
                    }.awaitAll()
                }
            }

        if (reactionAdded) {
            try {
                delay(5000)
                message.message.removeReaction(Emoji.fromUnicode("☄️")).queue()
            } catch (_: Exception) {
            }
        }
    }

    suspend fun processEdit(message: MessageUpdateEvent, sourceConnection: ConnectionsEntity) {
        MessagesEntity.SendMessage(
            message.messageIdLong,
            message.message.contentRaw,
            MessageReceivedEvent(message.jda, message.responseNumber, message.message),
            sourceConnection
        )

        val allDestinations = newSuspendedTransaction {
            ConnectionsEntity.find {
                (Connections.name eq sourceConnection.name) and
                        (Connections.channelId neq message.channel.idLong) and
                        (Connections.locked.isNull() or (Connections.locked eq false)) and
                        (Connections.paused.isNull() or (Connections.paused eq false))
            }.toList()
        }

        val seenChannels = mutableSetOf<Long>()
        val uniqueDestinations = allDestinations.filter { conn ->
            if (seenChannels.contains(conn.channelId)) {
                false
            } else {
                seenChannels.add(conn.channelId)
                true
            }
        }

        if (uniqueDestinations.isEmpty()) return

        val originalGuild = message.guild
        val content = message.message.contentRaw

        val processedAttachments = message.message.attachments.associate { attachment ->
            attachment.id to ProcessedAttachment(
                url = attachment.url,
                filename = attachment.fileName
            )
        }

        val reference = message.message.referencedMessage?.let { refMsg ->
            val (authorId, authorName, authorAvatar) = if (refMsg.author.isBot) {
                newSuspendedTransaction {
                    ForwardMessage.find {
                        ForwardMessages.forwardedMessageId eq refMsg.id
                    }.firstOrNull()?.let { forward ->
                        forward.originalAuthorId?.let { originalId ->
                            try {
                                message.jda.getUserById(originalId)?.let {
                                    Triple(it.id, it.name, it.avatarUrl)
                                } ?: Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                            } catch (_: Exception) {
                                Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                            }
                        } ?: Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                    } ?: Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
                }
            } else {
                Triple(refMsg.author.id, refMsg.author.name, refMsg.author.avatarUrl)
            }

            ReferenceMessage(
                message = refMsg,
                author = ReferenceAuthor(
                    userId = authorId,
                    userName = authorName,
                    avatarUrl = authorAvatar,
                    allowMentions = true
                ),
                data = ReferenceData(
                    originChannelId = message.channel.id,
                    originGuildId = message.guild.id,
                    forwardedMessageId = refMsg.id
                )
            )
        }

        val size = 10

        uniqueDestinations
            .chunked(size)
            .forEach { batch ->
                coroutineScope {
                    batch.map { dest ->
                        async {
                            try {
                                val targetChannel = message.jda.getTextChannelById(dest.channelId)
                                if (targetChannel == null) {
                                    println("Channel ${dest.channelId} not found")
                                    return@async
                                }

                                val destinationConnection = DestinationConnection(
                                    name = dest.name,
                                    messageComponentType = dest.messageComponentType,
                                    flags = dest.flags
                                )

                                ForwardHandler.forward(
                                    destinationConnection = destinationConnection,
                                    sourceMessage = message.message,
                                    sourceGuild = originalGuild,
                                    targetChannel = targetChannel,
                                    processedAttachments = processedAttachments,
                                    content = content,
                                    reference = reference,
                                    isEdit = true
                                )
                            } catch (e: Exception) {
                                println(e.message)
                            }
                        }
                    }.awaitAll()
                }
            }
    }
}