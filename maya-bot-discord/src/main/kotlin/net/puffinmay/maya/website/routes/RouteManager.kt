package net.puffinmay.maya.website.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import net.puffinmay.maya.website.MayaWebsite
import net.puffinmay.maya.website.routes.page.GetHomePage

fun Application.registerRoutes(server: MayaWebsite) {
    routing {
        get("/") { call.respondRedirect("/br/") }

        GetHomePage(server).install(this)

        staticResources("", "website/")
        staticResources("/v1/assets/css", "static/v1/assets/css")
        staticResources("/js/", "js/")
    }
}