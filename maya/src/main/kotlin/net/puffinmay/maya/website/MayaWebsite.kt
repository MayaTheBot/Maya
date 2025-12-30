package net.puffinmay.maya.website

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.puffinmay.common.MayaLocale
import net.puffinmay.maya.website.routes.RouteManager
import net.puffinmay.maya.MayaInstance
import net.puffinmay.maya.utils.serializable.MayaConfig
import net.puffinmay.common.website.UserSession
import java.util.concurrent.TimeUnit

class MayaWebsite(val instance: MayaInstance, val config: MayaConfig) {
    private val logger = KotlinLogging.logger {}
    private val json = Json { ignoreUnknownKeys = true }
    lateinit var locale: MayaLocale

    val isProduction = instance.config.environment == "production"
    private val server = embeddedServer(Netty, config.website.port) {
        install(ContentNegotiation) { json() }
        install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.path = "/"
                cookie.httpOnly = true
            }
        }

        routing {
            RouteManager(this@MayaWebsite).apply { registerRoutes() }
            staticResources("", "website/")
            staticResources("/v1/assets/css", "static/v1/assets/css")
            staticResources("/dashboard/assets/css", "static/dashboard/assets/css")
            staticResources("/js/", "js/")
            staticResources("/dashboard/js", "dashboard/js")
        }
    }

    init {
        logger.info { "MayaWebsite initialized in ${config.website.port}" }
        server.start(wait = false)
    }

    fun stop() {
        server.stop(10, 10, TimeUnit.SECONDS)
        logger.info { "Stopping website application..." }
    }
}