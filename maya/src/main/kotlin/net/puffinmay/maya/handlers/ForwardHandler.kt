package net.puffinmay.maya.handlers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.puffinmay.maya.database.dao.ForwardMessage
import net.puffinmay.maya.database.table.ForwardMessages
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

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

object OrbitConnectionFlags {
    const val Origin = 1 shl 0
}

val OrbitStaffId = listOf(
    "123456789",
    "987654321"
)

data object ForwardHandler {
    private const val EMOJI_REPLY = "<:troca:1440483651862270118>"
    private const val EMOJI_IMAGE = "<:images:1440483337662758924>"
    private const val EMOJI_MESSAGE = "<:messages:1440483149485445141>"
    private const val EMOJI_INFO = "<:information:1440439332438540349>"
    private const val EMOJI_VERIFIED = "<:verificado_orbit_ofc:1440483932859666657>"

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    fun formatMentions(content: String): String {
        return content
    }

    private fun extractMessageContentFromEmbed(embedDescription: String?): String {
        if (embedDescription.isNullOrBlank()) return ""

        val lines = embedDescription.lines()
        val contentLines = mutableListOf<String>()
        var foundReplySection = false

        for (line in lines) {
            when {
                line.startsWith(EMOJI_REPLY) -> {
                    foundReplySection = true
                    continue
                }

                foundReplySection && line.startsWith(">") -> {
                    continue
                }

                foundReplySection && line.startsWith(EMOJI_IMAGE) -> {
                    continue
                }

                foundReplySection && line.isBlank() -> {
                    foundReplySection = false
                    continue
                }

                line.contains("(editado)") -> {
                    contentLines.add(line.replace(" (editado)", "").trim())
                }

                else -> {
                    if (line.isNotBlank() && !line.startsWith(EMOJI_REPLY)) {
                        contentLines.add(line)
                    }
                }
            }
        }

        return contentLines.joinToString("\n").trim()
    }

    fun getMentionContent(reference: ReferenceMessage?, guildId: String): String {
        if (reference == null) return ""
        return if (reference.author.allowMentions && guildId == reference.data.originGuildId) {
            "<@${reference.author.userId}>"
        } else {
            ""
        }
    }

    suspend fun forward(
        client: JDA,
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
                                targetChannel = targetChannel,
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
                        targetChannel = targetChannel,
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
                                targetChannel.sendMessage(mentionContent).setEmbeds(embedPayload).complete()
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

    private fun createMessageEmbedPayload(
        targetChannel: TextChannel,
        guild: Guild,
        message: SanitizedMessage,
        reference: ReferenceMessage?,
        connection: DestinationConnection,
        data: Map<String, Any>,
        type: String,
        edit: Boolean = false
    ): MessageEmbed? {
        val content = data["content"] as? String ?: ""
        val attachments = (data["attachments"] as? List<*>)?.filterIsInstance<ProcessedAttachment>() ?: emptyList()

        if (type == "embed") {
            val descriptionParts = mutableListOf<String>()

            if (reference != null) {
                descriptionParts.add("$EMOJI_REPLY **Respondendo a ${reference.author.userName}:**")
                val quotedContent = if (reference.message.embeds.isNotEmpty()) {
                    extractMessageContentFromEmbed(reference.message.embeds.firstOrNull()?.description)
                } else reference.message.contentRaw.trim().ifEmpty {
                    ""
                }
                val quoted = formatMentions(quotedContent)
                val quotedAttachments = reference.message.attachments

                if (quotedAttachments.isNotEmpty()) {
                    descriptionParts.add(
                        quotedAttachments.mapIndexed { i, att ->
                            "$EMOJI_IMAGE [[imagem ${i + 1}]](${att.url})"
                        }.joinToString("\n")
                    )
                } else if (quoted.isNotEmpty()) {
                    descriptionParts.add(
                        if (quoted.length > 200) "> ${quoted.take(197)}..." else "> $quoted"
                    )
                } else {
                    descriptionParts.add("> *(mensagem vazia)*")
                }
                descriptionParts.add("")
            }

            val messageContent = mutableListOf<String>()
            if (content.isNotEmpty()) {
                messageContent.add(formatMentions(content))
            }

            if (attachments.isNotEmpty()) {
                messageContent.add(
                    attachments.mapIndexed { _, att ->
                        "$EMOJI_IMAGE [[${att.filename}]](${att.url})"
                    }.joinToString("\n")
                )
            }

            if (messageContent.isNotEmpty()) {
                descriptionParts.add(messageContent.joinToString("\n"))
            } else if (attachments.isEmpty() && content.isEmpty()) {
                descriptionParts.add("*(mensagem vazia)*")
            }

            val finalDescription = descriptionParts.joinToString("\n") + if (edit) " *(editado)*" else ""

            val embed = EmbedBuilder()
                .setAuthor(message.author.name, null, message.author.avatarUrl)
                .setDescription(finalDescription)
                .setColor(Color(0x5865F2))
                .setTimestamp(java.time.Instant.now())

            if (attachments.isNotEmpty()) {
                val firstImage = attachments.firstOrNull { att ->
                    att.url.contains(".png", ignoreCase = true) ||
                            att.url.contains(".jpg", ignoreCase = true) ||
                            att.url.contains(".jpeg", ignoreCase = true) ||
                            att.url.contains(".gif", ignoreCase = true) ||
                            att.url.contains(".webp", ignoreCase = true)
                }
                firstImage?.let { embed.setImage(it.url) }
            }

            if ((connection.flags and OrbitConnectionFlags.Origin) != 0) {
                embed.setFooter(
                    "${guild.name} • ${message.author.name} • ${dateFormat.format(Date())}",
                    guild.iconUrl
                )
            }

            return embed.build()
        }

        return null
    }

    private fun createFormattedMessageContent(
        originalGuildName: String,
        message: SanitizedMessage,
        content: String,
        attachments: List<ProcessedAttachment>,
        reference: ReferenceMessage?,
        targetChannelId: String,
        edit: Boolean = false
    ): String {
        var referenceBlock = ""
        if (reference != null) {
            val shouldMention = reference.author.allowMentions && targetChannelId == reference.data.originChannelId
            val authorDisplay = if (shouldMention) {
                "<@${reference.author.userId}>"
            } else {
                "`${reference.author.userName}`"
            }

            val refAttachments = reference.message.attachments
            if (refAttachments.isNotEmpty()) {
                referenceBlock = "$EMOJI_REPLY **Respondendo a $authorDisplay:**\n" +
                        refAttachments.mapIndexed { i, a ->
                            "$EMOJI_IMAGE [[imagem ${i + 1}]](${a.url})"
                        }.joinToString("\n") + "\n\n"
            } else {
                val refContentRaw = if (reference.message.embeds.isNotEmpty()) {
                    extractMessageContentFromEmbed(reference.message.embeds.firstOrNull()?.description)
                } else reference.message.contentRaw.trim().ifEmpty {
                    ""
                }

                if (refContentRaw.isNotEmpty()) {
                    val refContent = formatMentions(refContentRaw)
                    val snippet = if (refContent.length > 512) {
                        refContent.take(509) + "..."
                    } else {
                        refContent
                    }
                    referenceBlock = "$EMOJI_REPLY **Respondendo a $authorDisplay:**\n> $snippet\n\n"
                } else {
                    referenceBlock = "$EMOJI_REPLY **Respondendo a $authorDisplay:**\n> *(mensagem vazia)*\n\n"
                }
            }
        }

        val formattedMainContent = if (content.isNotEmpty()) {
            formatMentions(content.trim())
        } else {
            ""
        }

        val attachmentLinks = if (attachments.isNotEmpty()) {
            "\n" + attachments.mapIndexed { i, att ->
                "$EMOJI_IMAGE [[anexo ${i + 1}: ${att.filename}]](${att.url})"
            }.joinToString("\n")
        } else {
            ""
        }

        val editIndicator = if (edit) " *(editado)*" else ""

        val body = if (formattedMainContent.isNotEmpty()) {
            ">>> $formattedMainContent$attachmentLinks$editIndicator\n\n-# 👤 **${message.author.name}** • 🏠 **$originalGuildName**"
        } else if (attachments.isNotEmpty()) {
            ">>> $attachmentLinks$editIndicator\n\n-# 👤 **${message.author.name}** • 🏠 **$originalGuildName**"
        } else {
            ">>> *(mensagem vazia)*$editIndicator\n\n-# 👤 **${message.author.name}** • 🏠 **$originalGuildName**"
        }

        return referenceBlock + body
    }

    private fun createFormattedMessagePayload(
        originalGuildName: String,
        message: SanitizedMessage,
        content: String,
        attachments: List<ProcessedAttachment>,
        reference: ReferenceMessage?,
        targetChannelId: String,
        edit: Boolean = false
    ): MessageEditData {
        val contentString = createFormattedMessageContent(
            originalGuildName = originalGuildName,
            message = message,
            content = content,
            attachments = attachments,
            reference = reference,
            targetChannelId = targetChannelId,
            edit = edit
        )

        val builder = MessageEditBuilder()
            .setContent(contentString)

        return builder.build()
    }
}