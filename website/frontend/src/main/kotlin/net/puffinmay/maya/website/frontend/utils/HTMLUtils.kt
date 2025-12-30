package net.puffinmay.maya.website.frontend.utils

import io.ktor.server.routing.RoutingCall
import kotlinx.html.*
import net.puffinmay.common.MayaLocale

fun getLanguage(call: RoutingCall): MayaLocale {
    val lang = call.parameters["lang"]!!
    return MayaLocale(lang)
}

fun HEAD.buildHead(
    titleText: String,
    description: String,
    url: String? = "https://mayabot.fun",
    image: String? = "images/MayaAvatar.png",
    themeColor: String = "#e7385d",
    isDashboard: Boolean = false
) {
    meta(charset = "utf-8")
    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
    link(rel = "canonical", href = url)
    link(rel = "icon", href = "/assets/images/MayaAvatar.png")
    link(rel = "stylesheet", href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css")
    script(src = "/assets/js/theme.js") {}
    script(src = "/assets/js/navbar.js") {}
    script(src = "https://unpkg.com/htmx.org@1.9.10") {}
    script(src = "https://unpkg.com/hyperscript.org@0.9.12") { defer = true }

    if (isDashboard) {
        link(rel = "stylesheet", href = "/dashboard/assets/css/style.css") {
            attributes["type"] = "text/css"
        }

        link(rel = "stylesheet", href = "/v1/assets/css/global.css") {
            attributes["type"] = "text/css"
        }

        script(src = "/js/flandre.js") { defer = true }
        script(src = "/dashboard/js/dashboard-utils.js") { defer = true }
    } else {
        script(src = "/js/flandre.js") { defer = true }

        link(rel = "stylesheet", href = "/v1/assets/css/global.css") {
            attributes["type"] = "text/css"
        }

        link(rel = "stylesheet", href = "/v1/assets/css/style.css") {
            attributes["type"] = "text/css"
        }
    }

    metaProperty("og:type", "website")
    metaProperty("og:title", titleText)
    metaProperty("og:site_name", titleText)
    metaProperty("og:description", description)
    metaProperty("og:url", url!!)
    metaProperty("og:image", "/assets/$image")
    metaName("theme-color", themeColor, mapOf("data-react-helmet" to "true"))

    title { +titleText }

    script {
        unsafe {
            +"""
                window.dataLayer = window.dataLayer || [];
                function gtag() { dataLayer.push(arguments); }
                gtag('js', new Date());
                gtag('config', 'G-E3J7N4BP8L');
            """.trimIndent()
        }
    }
}

fun HEAD.metaProperty(property: String, content: String) {
    meta {
        attributes["property"] = property
        this.content = content
    }
}

fun HEAD.metaName(name: String, content: String, extraAttrs: Map<String, String> = emptyMap()) {
    meta {
        attributes["name"] = name
        this.content = content
        extraAttrs.forEach { (k, v) -> attributes[k] = v }
    }
}

fun FlowContent.footerSection() {
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
                        +"Sua assistente Discord inteligente e completa para melhorar a experiência do seu servidor."
                    }
                }

                div(classes = "footer-links") {
                    div(classes = "footer-column") {
                        h3(classes = "footer-title") { +"Navegação" }
                        a(href = "/br", classes = "footer-link") { +"Início" }
                        a(href = "/br/commands", classes = "footer-link") { +"Comandos" }
                        a(href = "/br/support", classes = "footer-link") { +"Suporte" }
                        a(href = "/br/dashboard", classes = "footer-link") { +"Dashboard" }
                    }

                    div(classes = "footer-column") {
                        h3(classes = "footer-title") { +"Legal" }
                        a(href = "/br/support/terms", classes = "footer-link") { +"Termos de Uso" }
                        a(href = "/br/support/privacy", classes = "footer-link") { +"Privacidade" }
                    }

                    div(classes = "footer-column") {
                        h3(classes = "footer-title") { +"Comunidade" }
                        a(href = "https://discord.gg/maya", classes = "footer-link", target = "_blank") {
                            i(classes = "fa-brands fa-discord") {}
                            +" Discord"
                        }
                        a(href = "https://github.com/maya", classes = "footer-link", target = "_blank") {
                            i(classes = "fa-brands fa-github") {}
                            +" GitHub"
                        }
                    }
                }
            }

            div(classes = "footer-bottom") {
                p(classes = "footer-copyright") {
                    +"© 2026 0xviny & Maya Studios. Todos os direitos reservados."
                }
            }
        }
    }
}