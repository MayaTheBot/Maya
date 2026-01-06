package net.puffinmay.maya.website.frontend.utils

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.h3
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.span
import net.puffinmay.common.MayaLocale

fun FlowContent.footerSection(locale: MayaLocale) {
    footer(classes = "maya-footer") {
        div(classes = "footer-container") {
            div(classes = "footer-content") {
                div(classes = "footer-brand") {
                    div(classes = "brand-section") {
                        img(src = "/assets/images/MayaAvatar.png", alt = "Maya") {
                            classes = setOf("footer-logo")
                        }
                        span(classes = "footer-brand-name") { +"Maya" }
                    }
                    p(classes = "footer-description") {
                        +locale["footer.description"]
                    }
                }

                div(classes = "footer-links") {
                    div(classes = "footer-column") {
                        h3(classes = "footer-title") { +locale["footer.links.navigation"] }
                        a(href = "/${locale.language}", classes = "footer-link") { +locale["footer.links.home"] }
                        a(href = "/${locale.language}/commands", classes = "footer-link") { +locale["footer.links.commands"] }
                        a(href = "/${locale.language}/support", classes = "footer-link") { +locale["footer.links.support"] }
                        a(href = "/${locale.language}/dashboard", classes = "footer-link") { +"Dashboard" }
                    }

                    div(classes = "footer-column") {
                        h3(classes = "footer-title") { +"Legal" }
                        a(href = "/${locale.language}/support/terms", classes = "footer-link") { +locale["footer.links.terms"] }
                        a(href = "/${locale.language}/support/privacy", classes = "footer-link") { +locale["footer.links.privacy"] }
                    }

                    div(classes = "footer-column") {
                        h3(classes = "footer-title") { +locale["footer.links.community"] }
                        a(href = "https://discord.gg/maya", classes = "footer-link", target = "_blank") {
                            i(classes = "fa-brands fa-discord") {}
                            +" Discord"
                        }
                        a(href = "https://github.com/MayaTheBot/Maya", classes = "footer-link", target = "_blank") {
                            i(classes = "fa-brands fa-github") {}
                            +" GitHub"
                        }
                    }
                }
            }

            div(classes = "footer-bottom") {
                p(classes = "footer-copyright") {
                    +locale["footer.copyright"]
                }
            }
        }
    }
}