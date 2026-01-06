package net.puffinmay.maya.handlers.utils

import net.puffinmay.common.ForwardConstants

fun formatMentions(content: String): String {
    return content
}

fun extractMessageContentFromEmbed(embedDescription: String?): String {
    if (embedDescription.isNullOrBlank()) return ""

    val lines = embedDescription.lines()
    val contentLines = mutableListOf<String>()
    var foundReplySection = false

    for (line in lines) {
        when {
            line.startsWith(ForwardConstants.EMOJI_REPLY) -> {
                foundReplySection = true
                continue
            }

            foundReplySection && line.startsWith(">") -> {
                continue
            }

            foundReplySection && line.startsWith(ForwardConstants.EMOJI_IMAGE) -> {
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
                if (line.isNotBlank() && !line.startsWith(ForwardConstants.EMOJI_REPLY)) {
                    contentLines.add(line)
                }
            }
        }
    }

    return contentLines.joinToString("\n").trim()
}

fun getMentionContent(reference: ReferenceMessage?, guildId: String): String {
    if (reference == null) return ""
    return if (guildId != reference.data.originGuildId) {
        "<@${reference.author.userId}>"
    } else {
        ""
    }
}