package net.puffinmay.maya.database.dao

import net.puffinmay.maya.database.table.AfkStatus
import net.puffinmay.maya.database.table.Blacklist
import net.puffinmay.maya.database.table.MessageLikes
import net.puffinmay.maya.database.table.Notifications
import net.puffinmay.maya.database.table.Partners
import net.puffinmay.maya.database.table.Premiums
import net.puffinmay.maya.database.table.SavedMessages
import net.puffinmay.maya.database.table.StaffMembers
import net.puffinmay.maya.database.table.Testimonials
import net.puffinmay.maya.database.table.Users
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class UsersEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UsersEntity>(Users)

    var allowMentions by Users.allowMentions
    var receiveNotifications by Users.receiveNotifications
    var about by Users.about
    var banner by Users.banner
    var border by Users.border
    var xp by Users.xp
    var level by Users.level
    var achievements by Users.achievements

    // relations (examples)
    val messageLikes by MessageLike referrersOn MessageLikes.user
    val savedMessages by SavedMessage referrersOn SavedMessages.user
}

class Premium(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Premium>(Premiums)

    var subscriptionId by Premiums.subscriptionId
    var activatedAt by Premiums.activatedAt
    var expiresAt by Premiums.expiresAt
    var renewsAt by Premiums.renewsAt
    var cancelsAt by Premiums.cancelsAt
    var userActiveId by Premiums.userActiveId
    var guild by GuildsEntity referencedOn Premiums.guild
}

class Staff(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Staff>(StaffMembers)

    var userId by StaffMembers.userId
}

class Partner(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Partner>(Partners)

    var userId by Partners.userId
}

class AfkStatusEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AfkStatusEntity>(AfkStatus)

    var reason by AfkStatus.reason
    var since by AfkStatus.since
    var mentions by AfkStatus.mentions
}

class BlacklistEntry(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BlacklistEntry>(Blacklist)

    var reason by Blacklist.reason
    var temp by Blacklist.temp
    var modId by Blacklist.modId
    var user by UsersEntity referencedOn Blacklist.user
}

class Notification(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Notification>(Notifications)

    var alertType by Notifications.alertType
    var message by Notifications.message
    var timestamp by Notifications.timestamp
    var read by Notifications.read
    var user by UsersEntity referencedOn Notifications.user
    var actorId by Notifications.actorId
    var originMessageId by Notifications.originMessageId
    var originChannelId by Notifications.originChannelId
    var originGuildId by Notifications.originGuildId
    var forwardedMessageId by Notifications.forwardedMessageId
    var forwardedChannelId by Notifications.forwardedChannelId
}

class Testimonial(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Testimonial>(Testimonials)

    var userId by Testimonials.userId
    var userName by Testimonials.userName
    var userAvatar by Testimonials.userAvatar
    var comment by Testimonials.comment
    var isApproved by Testimonials.isApproved
    var createdAt by Testimonials.createdAt
}