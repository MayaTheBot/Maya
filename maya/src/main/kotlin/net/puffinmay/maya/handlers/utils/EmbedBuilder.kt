package net.puffinmay.maya.handlers.utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.puffinmay.maya.utils.common.ForwardConstants
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

fun createMessageEmbedPayload(
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
            descriptionParts.add("${ForwardConstants.EMOJI_REPLY} **Respondendo a ${reference.author.userName}:**")
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
                        "${ForwardConstants.EMOJI_IMAGE} [[imagem ${i + 1}]](${att.url})"
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
                    "${ForwardConstants.EMOJI_IMAGE} [[${att.filename}]](${att.url})"
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