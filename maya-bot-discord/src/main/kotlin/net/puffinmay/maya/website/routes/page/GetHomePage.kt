package net.puffinmay.maya.website.routes.page

import io.ktor.server.routing.RoutingContext
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import net.puffinmay.common.website.UserSession
import net.puffinmay.maya.utils.website.BaseRoute
import net.puffinmay.maya.website.MayaWebsite
import net.puffinmay.maya.website.frontend.views.home.homePage
import net.puffinmay.maya.website.services.StatsService
import net.puffinmay.maya.website.utils.RouteUtils

class GetHomePage(val server: MayaWebsite) : BaseRoute("/{lang}/") {
    override suspend fun handle(context: RoutingContext) {
        val user = RouteUtils.checkSession(context.call, server, context.call.sessions.get<UserSession>())
        val stats = StatsService.collect(server.instance)

        RouteUtils.respondWithPage(context.call) { homePage(context.call, user, stats) }
    }
}