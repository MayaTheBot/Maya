package net.puffinmay.maya.api.routes

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import kotlinx.serialization.json.put
import io.ktor.server.routing.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import net.puffinmay.maya.MayaInstance

class GetMayaGuilds {
    fun Route.getMayaGuilds(instance: MayaInstance) {
        get("/guilds") {
            val serverCount = instance.shardManager.shards.sumOf { it.guilds.size }

            val response = buildJsonObject {
                put("serverCount", serverCount)
            }

            val jsonString = Json.encodeToString(response)

            call.respondText(
                contentType = ContentType.Application.Json,
                text = jsonString
            )
        }
    }
}