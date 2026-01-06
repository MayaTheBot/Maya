package net.puffinmay.maya.database.dao

import net.puffinmay.maya.database.table.Connections
import net.puffinmay.maya.database.table.Guilds
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GuildsEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GuildsEntity>(Guilds)

    var prefix by Guilds.prefix
    var cases by Guilds.cases
    var language by Guilds.language
    var createdAt by Guilds.createdAt

    val MayaConnections by ConnectionsEntity referrersOn Connections.guild
}