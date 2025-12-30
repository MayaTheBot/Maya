package net.puffinmay.maya.website.routes

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.json.Json
import net.puffinmay.maya.website.MayaWebsite
import net.puffinmay.maya.website.frontend.pages.home.homePage
import net.puffinmay.maya.website.utils.RouteUtils.respondWithPage

class RouteManager(val server: MayaWebsite) {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    fun Routing.registerRoutes() {
        get("/") {
            call.respondRedirect("/br/")
        }

        get("/{lang}/") { respondWithPage { homePage(call, server.isProduction) } }
    }
}