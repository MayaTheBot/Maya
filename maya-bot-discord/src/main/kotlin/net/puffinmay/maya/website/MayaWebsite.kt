package net.puffinmay.maya.website

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.sessions.SameSite
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sameSite
import mu.KotlinLogging
import net.puffinmay.common.MayaLocale
import net.puffinmay.maya.MayaInstance
import net.puffinmay.maya.utils.serializable.MayaConfig
import net.puffinmay.common.website.UserSession
import net.puffinmay.maya.website.routes.registerRoutes

class MayaWebsite(val instance: MayaInstance, val config: MayaConfig) {
    private val logger = KotlinLogging.logger {}
    var generateHmac = instance.utils::generateHmac
    lateinit var locale: MayaLocale

    private val server = embeddedServer(Netty, config.website.port) {
        install(ContentNegotiation) { json() }
        install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.path = "/"
                cookie.httpOnly = true
                cookie.sameSite = SameSite.Lax
                cookie.maxAgeInSeconds = 2592000
            }
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

        registerRoutes(this@MayaWebsite)
    }

    init {
        logger.info { "MayaWebsite initialized in ${config.website.port}" }
        server.start(wait = false)
    }
}