package net.puffinmay.common

import java.text.SimpleDateFormat
import java.util.Locale

enum class MayaConnectionFlag(val value: Int) {
    Frozen(1 shl 1),
    AllowFiles(1 shl 2),
    AllowInvites(1 shl 3),
    AllowLinks(1 shl 4),
    NoIdentification(1 shl 5),
    AllowOrigin(1 shl 6),
    AllowWebhooks(1 shl 7),
    AllowEmojis(1 shl 8),
    CompactMode(1 shl 9),
    ConfirmActions(1 shl 10),
    AutoTranslate(1 shl 11),
    Inactive(1 shl 12),
    AllowMentions(1 shl 13),
    AllowWallOfText(1 shl 14),
    AutoModIntelligence(1 shl 15),
    DisableReactionAndSystemMessages(1 shl 16),
    EnableModeTosco(1 shl 17),
    CleanMessage(1 shl 18),
    UseComponentsV2(1 shl 19),
    NameServerWebhook(1 shl 20)
}

object ForwardConstants {
    const val EMOJI_REPLY = "<:troca:1440483651862270118>"
    const val EMOJI_IMAGE = "<:images:1440483337662758924>"
    const val EMOJI_MESSAGE = "<:messages:1440483149485445141>"
    const val EMOJI_INFO = "<:information:1440439332438540349>"
    const val EMOJI_VERIFIED = "<:verificado_orbit_ofc:1440483932859666657>"

    @Suppress("DEPRECATION")
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
}
