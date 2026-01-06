package net.puffinmay.maya.website.frontend.views.home

import io.ktor.server.routing.RoutingCall
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import net.puffinmay.common.website.StatsResponse
import net.puffinmay.common.website.UserSession
import net.puffinmay.maya.website.frontend.utils.buildHead
import net.puffinmay.maya.website.frontend.utils.footerSection
import net.puffinmay.maya.website.frontend.utils.getLanguage
import net.puffinmay.maya.website.frontend.utils.headerWithUser

data class Partner(val name: String, val image: String)

fun homePage(call: RoutingCall, userSession: UserSession?, stats: StatsResponse): String {
    val partners = listOf(
        Partner("Maya Home", "/assets/images/MayaAvatar.png"),
        Partner("Braixen's House", "/assets/images/MayaAvatar.png"),
    )

    return createHTML().html {
        val locale = getLanguage(call)

        head {
            buildHead(
                titleText = "Maya - A melhor bot de conexões para o seu servidor!",
                description = "Olá, me chamo Maya, um bot feito para o seu servidor, contendo sistemas únicos e fáceis de configurar, me adicione ao seu servidor e veja meu poder."
            )
        }

        body {
            headerWithUser(userSession, locale)

            main {
                div("hero") {
                    div("pattern")

                    div("hero-content") {
                        h2("hero-title") {
                            +locale["website.homepage.greetings"]
                            span("maya-name") {
                                +" Maya"
                            }
                        }

                        div("hero-description") {
                            +locale["website.homepage.description"]
                        }

                        div("hero-actions") {
                            a(href = "/add") {
                                button(classes = "primary-button", type = ButtonType.button) {
                                    +locale["website.homepage.addButton"]
                                }
                            }

                            a(href = "/${locale.language}/dashboard") {
                                button(classes = "secondary-button", type = ButtonType.button) {
                                    +locale["website.homepage.manageGuilds"]
                                }
                            }
                        }
                    }

                    div("hero-image") {
                        img {
                            src = "/assets/images/MayaAvatar.png"
                            alt = "Maya Bot"
                            id = "maya-avatar"
                        }
                    }

                    div("waves-container") {
                        unsafe {
                            raw(
                                """
                            <svg class="wave-svg wave-back" viewBox="0 0 1440 490" xmlns="http://www.w3.org/2000/svg">
                                <path d="M0,392L30,400.2C60,408,120,425,180,367.5C240,310,300,180,360,163.3C420,147,480,245,540,310.3C600,376,660,408,720,400.2C780,392,840,343,900,302.2C960,261,1020,229,1080,245C1140,261,1200,327,1260,367.5C1320,408,1380,425,1440,359.3C1500,294,1560,147,1620,98C1680,49,1740,98,1800,130.7C1860,163,1920,180,1980,220.5C2040,261,2100,327,2160,302.2C2220,278,2280,163,2340,138.8C2400,114,2460,180,2520,187.8C2580,196,2640,147,2700,106.2C2760,65,2820,33,2880,73.5C2940,114,3000,229,3060,302.2C3120,376,3180,408,3240,408.3C3300,408,3360,376,3420,302.2C3480,229,3540,114,3600,98C3660,82,3720,163,3780,236.8C3840,310,3900,376,3960,367.5C4020,359,4080,278,4140,245C4200,212,4260,229,4290,236.8L4320,245L4320,490L4290,490C4260,490,4200,490,4140,490C4080,490,4020,490,3960,490C3900,490,3840,490,3780,490C3720,490,3660,490,3600,490C3540,490,3480,490,3420,490C3360,490,3300,490,3240,490C3180,490,3120,490,3060,490C3000,490,2940,490,2880,490C2820,490,2760,490,2700,490C2640,490,2580,490,2520,490C2460,490,2400,490,2340,490C2280,490,2220,490,2160,490C2100,490,2040,490,1980,490C1920,490,1860,490,1800,490C1740,490,1680,490,1620,490C1560,490,1500,490,1440,490C1380,490,1320,490,1260,490C1200,490,1140,490,1080,490C1020,490,960,490,900,490C840,490,780,490,720,490C660,490,600,490,540,490C480,490,420,490,360,490C300,490,240,490,180,490C120,490,60,490,30,490L0,490Z"></path>
                            </svg>
                            """
                            )
                        }

                        unsafe {
                            raw(
                                """
                            <svg class="wave-svg wave-middle" viewBox="0 0 1440 490" xmlns="http://www.w3.org/2000/svg">
                                <path d="M0,98L30,130.7C60,163,120,229,180,236.8C240,245,300,196,360,196C420,196,480,245,540,294C600,343,660,392,720,343C780,294,840,147,900,81.7C960,16,1020,33,1080,98C1140,163,1200,278,1260,334.8C1320,392,1380,392,1440,343C1500,294,1560,196,1620,155.2C1680,114,1740,131,1800,147C1860,163,1920,180,1980,155.2C2040,131,2100,65,2160,81.7C2220,98,2280,196,2340,220.5C2400,245,2460,196,2520,212.3C2580,229,2640,310,2700,359.3C2760,408,2820,425,2880,367.5C2940,310,3000,180,3060,171.5C3120,163,3180,278,3240,302.2C3300,327,3360,261,3420,245C3480,229,3540,261,3600,269.5C3660,278,3720,261,3780,261.3C3840,261,3900,278,3960,277.7C4020,278,4080,261,4140,285.8C4200,310,4260,376,4290,408.3L4320,441L4320,490L4290,490C4260,490,4200,490,4140,490C4080,490,4020,490,3960,490C3900,490,3840,490,3780,490C3720,490,3660,490,3600,490C3540,490,3480,490,3420,490C3360,490,3300,490,3240,490C3180,490,3120,490,3060,490C3000,490,2940,490,2880,490C2820,490,2760,490,2700,490C2640,490,2580,490,2520,490C2460,490,2400,490,2340,490C2280,490,2220,490,2160,490C2100,490,2040,490,1980,490C1920,490,1860,490,1800,490C1740,490,1680,490,1620,490C1560,490,1500,490,1440,490C1380,490,1320,490,1260,490C1200,490,1140,490,1080,490C1020,490,960,490,900,490C840,490,780,490,720,490C660,490,600,490,540,490C480,490,420,490,360,490C300,490,240,490,180,490C120,490,60,490,30,490L0,490Z"></path>
                            </svg>
                            """
                            )
                        }

                        unsafe {
                            raw(
                                """
                            <svg class="wave-svg wave-front" viewBox="0 0 1440 490" xmlns="http://www.w3.org/2000/svg">
                                <path d="M0,343L30,326.7C60,310,120,278,180,277.7C240,278,300,310,360,343C420,376,480,408,540,375.7C600,343,660,245,720,187.8C780,131,840,114,900,98C960,82,1020,65,1080,98C1140,131,1200,212,1260,228.7C1320,245,1380,196,1440,179.7C1500,163,1560,180,1620,155.2C1680,131,1740,65,1800,98C1860,131,1920,261,1980,302.2C2040,343,2100,294,2160,269.5C2220,245,2280,245,2340,228.7C2400,212,2460,180,2520,196C2580,212,2640,278,2700,261.3C2760,245,2820,147,2880,163.3C2940,180,3000,310,3060,375.7C3120,441,3180,441,3240,432.8C3300,425,3360,408,3420,375.7C3480,343,3540,294,3600,253.2C3660,212,3720,180,3780,147C3840,114,3900,82,3960,114.3C4020,147,4080,245,4140,285.8C4200,327,4260,310,4290,302.2L4320,294L4320,490L4290,490C4260,490,4200,490,4140,490C4080,490,4020,490,3960,490C3900,490,3840,490,3780,490C3720,490,3660,490,3600,490C3540,490,3480,490,3420,490C3360,490,3300,490,3240,490C3180,490,3120,490,3060,490C3000,490,2940,490,2880,490C2820,490,2760,490,2700,490C2640,490,2580,490,2520,490C2460,490,2400,490,2340,490C2280,490,2220,490,2160,490C2100,490,2040,490,1980,490C1920,490,1860,490,1800,490C1740,490,1680,490,1620,490C1560,490,1500,490,1440,490C1380,490,1320,490,1260,490C1200,490,1140,490,1080,490C1020,490,960,490,900,490C840,490,780,490,720,490C660,490,600,490,540,490C480,490,420,490,360,490C300,490,240,490,180,490C120,490,60,490,30,490L0,490Z"></path>
                            </svg>
                            """
                            )
                        }
                    }

                    div("scroll-down") {
                        a(href = "#next-section") {
                            i(classes = "fa-solid fa-chevron-down") {}
                        }
                    }
                }

                div("next-section feature") {
                    id = "next-section"

                    h2("feature-main-title") {
                        span("maya-name") {
                            +"Maya "
                        }
                        +locale["website.homepage.feature.title"]
                    }

                    div("feature-container") {
                        div("feature-image-wrapper") {
                            img {
                                src = "/assets/images/MapaMundi.png"
                                alt = "Mapa mundi"
                                id = "MapaMundi"
                            }
                        }

                        div("feature-content-wrapper") {
                            h3("feature-subtitle") {
                                +locale["website.homepage.feature.global.title"]
                            }

                            div("feature-description") {
                                +locale["website.homepage.feature.global.description"]
                            }
                        }
                    }

                    div("feature-container") {
                        div("feature-content-wrapper") {
                            h3("feature-subtitle") {
                                +locale["website.homepage.feature.security.title"]
                            }

                            div("feature-description") {
                                +locale["website.homepage.feature.security.description"]
                            }
                        }

                        div("feature-image-wrapper") {
                            img {
                                src = "/assets/images/Cadeado.png"
                                alt = "Cadeado"
                                id = "Cadeado"
                            }
                        }
                    }

                    div("feature-container") {
                        div("feature-image-wrapper") {
                            img {
                                src = "/assets/images/Clock.png"
                                alt = "Relógio"
                                id = "Relogio"
                            }
                        }

                        div("feature-content-wrapper") {
                            h3("feature-subtitle") {
                                +locale["website.homepage.feature.velocity.title"]
                            }

                            div("feature-description") {
                                +locale["website.homepage.feature.velocity.description"]
                            }
                        }
                    }
                }

                div("stats") {
                    div("pattern")

                    h2("stats-main-title") {
                        span("maya-name") {
                            +"Maya "
                        }
                        +locale["website.homepage.stats.title"]
                    }

                    div("stats-container") {
                        div("stats-content") {
                            h3("stats-subtitle") {
                                i("fa-solid fa-server")
                                +locale["website.homepage.stats.servers.title"]
                            }

                            div("stats-description") {
                                +stats.serverCount.toString()
                            }
                        }

                        div("line-block")

                        div("stats-content") {
                            h3("stats-subtitle") {
                                i("fa-solid fa-terminal")
                                +locale["website.homepage.stats.commands.title"]
                            }

                            div("stats-description") {
                                +stats.commandsCount.toString()
                            }
                        }

                        div("line-block")

                        div("stats-content") {
                            h3("stats-subtitle") {
                                i("fa-regular fa-user")
                                +locale["website.homepage.stats.users.title"]
                            }

                            div("stats-description") {
                                +stats.usersCount.toString()
                            }
                        }

                        div("line-block")

                        div("stats-content") {
                            h3("stats-subtitle") {
                                i("fa-regular fa-message")
                                +locale["website.homepage.stats.messages.title"]
                            }

                            div("stats-description") {
                                +stats.messagesCount.toString()
                            }
                        }
                    }
                }

                div("servers-partner") {
                    h2("partner-title") {
                        +locale["website.homepage.partners.title"]
                    }

                    div("partner-carousel") {
                        div("partner-track") {
                            repeat(4) {
                                partners.forEach { partner ->
                                    div("partner-item") {
                                        img(src = partner.image, alt = partner.name)
                                        span("partner-name") {
                                            +partner.name
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                div("add-bot") {
                    div("add-bot-pattern")

                    div("add-bot-inner") {
                        div("add-bot-card") {
                            h2("add-bot-title") {
                                +locale["website.homepage.addbot.title"]
                            }

                            p("add-bot-description") {
                                +locale["website.homepage.addbot.description"]
                            }

                            div("add-bot-actions") {
                                a(href = "/add") {
                                    button(classes = "primary-button", type = ButtonType.button) {
                                        +locale["website.homepage.addButton"]
                                    }
                                }
                            }
                        }

                        div("add-bot-image-wrapper") {
                            img {
                                src = "/assets/images/MayaAvatar.png"
                                alt = "Maya avatar"
                                id = "add-maya-avatar"
                            }
                        }
                    }
                }
            }

            footerSection(locale)
        }
    }
}