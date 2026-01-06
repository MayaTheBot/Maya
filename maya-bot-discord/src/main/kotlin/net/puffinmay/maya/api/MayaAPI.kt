package net.puffinmay.maya.api

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import mu.KotlinLogging
import net.puffinmay.maya.MayaInstance
import net.puffinmay.common.Constants
import net.puffinmay.maya.api.routes.GetMayaGuilds

class MayaAPI(val instance: MayaInstance) {
    private val logger = KotlinLogging.logger { }
    private val server = embeddedServer(Netty, port = instance.config.internal.apiPort) {
        install(ContentNegotiation) {
            json()
        }

        install(Authentication) {
            bearer("auth-bearer") {
                authenticate { tokenCredential ->
                    if (tokenCredential.token == instance.config.internal.apiKey) {
                        UserIdPrincipal("authenticatedUser")
                    } else null
                }
            }
        }

        routing {
            get("/") {
                call.respondRedirect(Constants.MAYA_WEBSITE)
            }

            get("/health") {
                call.respondText("OK")
            }

            authenticate("auth-bearer") {
                GetMayaGuilds().apply { getMayaGuilds(instance) }
            }
        }
    }.start(wait = false)

    fun stop() {
        server.stop()
        logger.info { "Maya API stopped successful" }
    }
}