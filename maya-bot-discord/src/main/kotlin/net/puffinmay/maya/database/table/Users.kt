package net.puffinmay.maya.database.table

import net.puffinmay.common.Enums
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object Users : UUIDTable("users") {
    val allowMentions = bool("allow_mentions").default(true)
    val receiveNotifications = bool("receive_notifications").default(true)
    val about = text("about").nullable()
    val banner = varchar("banner", 255).nullable()
    val border = varchar("border", 100).nullable()
    val xp = integer("xp").default(0)
    val level = integer("level").default(1)
    val achievements = text("achievements").default("[]") // json array stored as text
}

object Premiums : UUIDTable("premium") {
    val subscriptionId = varchar("subscription_id", 255)
    val activatedAt = long("activated_at").clientDefault { System.currentTimeMillis() }
    val expiresAt = long("expires_at")
    val renewsAt = long("renews_at").nullable()
    val cancelsAt = long("cancels_at").nullable()
    val userActiveId = uuid("user_active_id")
    val guild = reference("guild_id", Guilds, onDelete = ReferenceOption.CASCADE).uniqueIndex()
}

object Partners : UUIDTable("partners") {
    val userId = uuid("user_id").uniqueIndex()
}

object StaffMembers : UUIDTable("staff_members") {
    val userId = uuid("user_id").uniqueIndex()
}

object AfkStatus : UUIDTable("afk_status") {
    val reason = varchar("reason", 500)
    val since = long("since").clientDefault { System.currentTimeMillis() }
    val mentions = text("mentions").default("[]")
}

object Blacklist : UUIDTable("blacklist") {
    val reason = varchar("reason", 500)
    val temp = long("temp").nullable()
    val modId = varchar("mod_id", 255)
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE).uniqueIndex()
}

object Notifications : UUIDTable("notifications") {
    val alertType = enumerationByName("alert_type", 20, Enums.AlertType::class)
    val message = text("message")
    val timestamp = long("timestamp").clientDefault { System.currentTimeMillis() }
    val read = bool("read").default(false)
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val actorId = varchar("actor_id", 255).nullable()
    val originMessageId = varchar("origin_message_id", 255).nullable()
    val originChannelId = varchar("origin_channel_id", 255).nullable()
    val originGuildId = varchar("origin_guild_id", 255).nullable()
    val forwardedMessageId = varchar("forwarded_message_id", 255).nullable()
    val forwardedChannelId = varchar("forwarded_channel_id", 255).nullable()
}

object Testimonials : UUIDTable("testimonials") {
    val userId = uuid("user_id")
    val userName = varchar("user_name", 200)
    val userAvatar = varchar("user_avatar", 255).nullable()
    val comment = text("comment")
    val isApproved = bool("is_approved").default(false)
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
}