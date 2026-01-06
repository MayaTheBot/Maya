package net.puffinmay.maya.utils.common

import java.io.File
import kotlin.system.exitProcess

fun checkConfigFile(): File {
    val path = System.getenv("CONF")
        ?: System.getProperty("conf")
        ?: "maya-bot-discord/src/main/resources/maya.conf"

    val configFile = File(path)

    if (!configFile.exists()) {
        println("O arquivo 'maya.conf' não foi encontrado em: ${configFile.absolutePath}")
        exitProcess(1)
    }

    return configFile
}
