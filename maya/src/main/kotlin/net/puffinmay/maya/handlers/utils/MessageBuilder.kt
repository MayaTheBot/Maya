package net.puffinmay.maya.handlers.utils

import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.puffinmay.maya.utils.common.ForwardConstants

fun createFormattedMessageContent(
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
            referenceBlock = "${ForwardConstants.EMOJI_REPLY} **Respondendo a $authorDisplay:**\n" +
                    refAttachments.mapIndexed { i, a ->
                        "${ForwardConstants.EMOJI_IMAGE} [[imagem ${i + 1}]](${a.url})"
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
                referenceBlock = "${ForwardConstants.EMOJI_REPLY} **Respondendo a $authorDisplay:**\n> $snippet\n\n"
            } else {
                referenceBlock =
                    "${ForwardConstants.EMOJI_REPLY} **Respondendo a $authorDisplay:**\n> *(mensagem vazia)*\n\n"
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
            "${ForwardConstants.EMOJI_IMAGE} [[anexo ${i + 1}: ${att.filename}]](${att.url})"
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

fun createFormattedMessagePayload(
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