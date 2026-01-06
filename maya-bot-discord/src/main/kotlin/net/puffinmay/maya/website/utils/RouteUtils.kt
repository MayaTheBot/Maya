package net.puffinmay.maya.website.utils

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import net.puffinmay.common.website.UserSession
import net.puffinmay.maya.website.MayaWebsite

object RouteUtils {
    suspend fun respondWithPage(call: RoutingCall, statusCode: HttpStatusCode? = null, provider: suspend () -> String) {
        call.respondText(ContentType.Text.Html, statusCode ?: HttpStatusCode.OK, provider)
    }

    fun checkSession(call: RoutingCall, server: MayaWebsite, session: UserSession?): UserSession? {
        if (session == null) return null

        val expectedHmac = server.generateHmac("${session.userId}:${session.accessToken}")

        return if (session.hmac == expectedHmac) {
            session
        } else {
            call.sessions.clear<UserSession>()
            null
        }
    }
}