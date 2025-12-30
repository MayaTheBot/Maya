package net.puffinmay.maya.website.frontend.utils

import io.ktor.server.routing.RoutingCall
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.html.*
import net.puffinmay.common.MayaLocale
import net.puffinmay.common.website.UserSession

fun FlowContent.headerWithUser(call: RoutingCall, locale: MayaLocale) {
    val user = call.sessions.get<UserSession>()

    header {
        nav("maya-navbar") {
            id = "navigation-bar"

            div("navbar-container") {
                div("navbar-left") {
                    button(classes = "menu-toggle") {
                        id = "menu-toggle"
                        type = ButtonType.button
                        i("fa-solid fa-bars") {
                            id = "menu-icon"
                        }
                    }

                    a(href = "/", classes = "brand-link") {
                        div("brand-content") {
                            img(src = "/assets/images/MayaAvatar.png", alt = "Maya") {
                                classes = setOf("brand-logo")
                            }
                            span("brand-name") { +"Maya" }
                        }
                    }

                    div("navbar-links desktop-only") {
                        a(href = "/$locale/support", classes = "nav-link") {
                            i("fa-solid fa-headset") {}
                            span { +locale["header.support"] }
                        }
                        a(href = "/$locale/support/terms", classes = "nav-link") {
                            i("fa-solid fa-file-contract") {}
                            span { +locale["header.termsOfUse"] }
                        }
                        a(href = "/$locale/commands", classes = "nav-link") {
                            i("fa-solid fa-terminal") {}
                            span { +locale["header.commands"] }
                        }
                    }
                }

                div("navbar-right") {
                    div("language desktop-only") {
                        button(classes = "language-toggle") {
                            id = "language-toggle-btn"
                            type = ButtonType.button
                            attributes["aria-haspopup"] = "true"
                            attributes["aria-expanded"] = "false"
                            img {
                                src = "/assets/images/language-solid.svg"
                                alt = "Language"
                                classes = setOf("language-icon")
                            }
                        }
                        div("language-dropdown") {
                            attributes["role"] = "menu"
                            button(classes = "language-option") {
                                type = ButtonType.button
                                attributes["data-lang"] = "pt-BR"
                                span("flag") { +"🇧🇷" }
                                span("lang-text") { +"Português (Brasil)" }
                            }
                            button(classes = "language-option") {
                                type = ButtonType.button
                                attributes["data-lang"] = "en-US"
                                span("flag") { +"🇺🇸" }
                                span("lang-text") { +"English (USA)" }
                            }
                        }
                    }

                    button(classes = "theme-toggle") {
                        id = "theme-toggle-btn"
                        type = ButtonType.button
                        i("fa-solid fa-moon") {}
                    }

                    if (user != null) {
                        a(href = "/$locale/dashboard", classes = "user-profile") {
                            img("https://cdn.discordapp.com/avatars/${user.userId}/${user.avatar}?size=128", "Avatar") {
                                classes = setOf("user-avatar")
                            }
                            div("user-info desktop-only") {
                                span("user-name") { +user.globalName }
                                i("fa-solid fa-chevron-down") {}
                            }
                        }
                    } else {
                        a(href = "/$locale/dashboard", classes = "login-btn") {
                            i("fa-solid fa-sign-in-alt") {}
                            span { +locale["header.login"] }
                        }
                    }
                }
            }

            div("mobile-menu") {
                id = "mobile-menu"

                div("mobile-menu-content") {
                    div("mobile-menu-links") {
                        a(href = "/$locale/support", classes = "mobile-link") {
                            i("fa-solid fa-headset") {}
                            span { +locale["header.support"] }
                        }
                        a(href = "/$locale/support/terms", classes = "mobile-link") {
                            i("fa-solid fa-file-contract") {}
                            span { +locale["header.termsOfUse"] }
                        }
                        a(href = "/$locale/commands", classes = "mobile-link") {
                            i("fa-solid fa-terminal") {}
                            span { +locale["header.commands"] }
                        }

                        div("mobile-language-section") {
                            button(classes = "mobile-language") {
                                id = "mobile-language-btn"
                                type = ButtonType.button
                                span("mobile-language-label") {
                                    i("fa-solid fa-globe") {}
                                    +locale["header.language"]
                                }
                                i("fa-solid fa-chevron-down mobile-chevron") {}
                            }

                            div("mobile-language-dropdown") {
                                id = "mobile-language-dropdown"
                                button(classes = "mobile-language-option") {
                                    type = ButtonType.button
                                    attributes["data-lang"] = "pt-BR"
                                    span("flag") { +"🇧🇷" }
                                    span("lang-text") { +"Português (Brasil)" }
                                }
                                button(classes = "mobile-language-option") {
                                    type = ButtonType.button
                                    attributes["data-lang"] = "en-US"
                                    span("flag") { +"🇺🇸" }
                                    span("lang-text") { +"English (USA)" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}