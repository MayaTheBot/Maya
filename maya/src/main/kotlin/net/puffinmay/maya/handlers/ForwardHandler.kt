package net.puffinmay.maya.handlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.puffinmay.maya.database.dao.ForwardMessage
import net.puffinmay.maya.database.table.ForwardMessages
import net.puffinmay.maya.handlers.utils.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

data object ForwardHandler {
    suspend fun forward(
        destinationConnection: DestinationConnection,
        sourceMessage: Message,
        sourceGuild: Guild,
        targetChannel: TextChannel,
        processedAttachments: Map<String, ProcessedAttachment>,
        content: String,
        reference: ReferenceMessage?,
        isEdit: Boolean
    ): ForwardResult {
        val sanitizedSourceMessage = SanitizedMessage(
            id = sourceMessage.id,
            content = content,
            channelId = sourceMessage.channel.id,
            author = MessageAuthor(
                id = sourceMessage.author.id,
                name = sourceMessage.author.name,
                avatarUrl = sourceMessage.author.avatarUrl
            )
        )
        val attachmentsToSend = processedAttachments.values.toList()

        if (isEdit) {
            val existingForward = newSuspendedTransaction {
                ForwardMessage.find {
                    (ForwardMessages.originMessageId eq sourceMessage.id) and
                            (ForwardMessages.forwardedChannelId eq targetChannel.idLong.toString())
                }.firstOrNull()
            }

            if (existingForward != null) {
                try {
                    when (destinationConnection.messageComponentType) {
                        1 -> {
                            val payload = createMessageEmbedPayload(
                                guild = sourceGuild,
                                message = sanitizedSourceMessage,
                                reference = reference,
                                connection = destinationConnection,
                                data = mapOf("content" to content, "attachments" to attachmentsToSend),
                                type = "embed",
                                edit = true
                            )
                            if (payload != null) {
                                withContext(Dispatchers.IO) {
                                    targetChannel.editMessageEmbedsById(existingForward.forwardedMessageId, payload)
                                        .complete()
                                }
                            }
                        }

                        0 -> {
                            val payload = createFormattedMessagePayload(
                                originalGuildName = sourceGuild.name,
                                message = sanitizedSourceMessage,
                                content = content,
                                attachments = attachmentsToSend,
                                reference = reference,
                                targetChannelId = targetChannel.id,
                                edit = true
                            )
                            withContext(Dispatchers.IO) {
                                targetChannel.editMessageById(existingForward.forwardedMessageId, payload).complete()
                            }
                        }
                    }
                    return ForwardResult(success = true)
                } catch (err: Exception) {
                    if (err.message?.contains("10008") == true ||
                        err.message?.contains("50001") == true ||
                        err.message?.contains("10003") == true
                    ) {
                        newSuspendedTransaction {
                            existingForward.delete()
                        }
                    }
                }
            }
        }

        return try {
            var sentMessage: Message? = null

            when (destinationConnection.messageComponentType) {
                1 -> {
                    val embedPayload = createMessageEmbedPayload(
                        guild = sourceGuild,
                        message = sanitizedSourceMessage,
                        reference = reference,
                        connection = destinationConnection,
                        data = mapOf("content" to content, "attachments" to attachmentsToSend),
                        type = "embed"
                    )

                    if (embedPayload != null) {
                        val mentionContent = getMentionContent(reference, targetChannel.guild.id)
                        sentMessage = if (mentionContent.isNotEmpty()) {
                            withContext(Dispatchers.IO) {
                                val mentions =
                                    if (targetChannel.id != reference!!.data.originChannelId)
                                        listOf(reference.author.userId)
                                    else
                                        emptyList()

                                targetChannel.sendMessage(mentionContent).setEmbeds(embedPayload)
                                    .mentionUsers(mentions).complete()
                            }
                        } else {
                            withContext(Dispatchers.IO) {
                                targetChannel.sendMessageEmbeds(embedPayload).complete()
                            }
                        }
                    }
                }

                0 -> {
                    val payload = createFormattedMessageContent(
                        originalGuildName = sourceGuild.name,
                        message = sanitizedSourceMessage,
                        content = content,
                        attachments = attachmentsToSend,
                        reference = reference,
                        targetChannelId = targetChannel.id,
                        edit = false
                    )
                    sentMessage = withContext(Dispatchers.IO) {
                        targetChannel.sendMessage(payload).complete()
                    }
                }

                else -> {
                    return ForwardResult(
                        success = false,
                        reason = "Tipo de componente de mensagem invalido: ${destinationConnection.messageComponentType}"
                    )
                }
            }

            if (sentMessage == null) {
                throw Exception("A mensagem encaminhada resultou em nula apos o envio.")
            }

            newSuspendedTransaction {
                ForwardMessage.new {
                    originMessageId = sourceMessage.id
                    originChannelId = sourceMessage.channel.id
                    forwardedMessageId = sentMessage.id
                    forwardedChannelId = targetChannel.idLong.toString()
                    connectionName = destinationConnection.name
                    originalAuthorId = sourceMessage.author.id
                }
            }

            ForwardResult(success = true, sentMessage = sentMessage)
        } catch (err: Exception) {
            ForwardResult(success = false, reason = err.message ?: "Um erro desconhecido ocorreu.")
        }
    }
}