package net.puffinmay.maya

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import net.puffinmay.common.Constants
import net.puffinmay.maya.utils.HoconUtils.decodeFromString
import net.puffinmay.maya.utils.HostnameUtils
import net.puffinmay.maya.utils.common.checkConfigFile
import net.puffinmay.maya.utils.installCoroutinesDebugProbes
import net.puffinmay.maya.utils.serializable.MayaConfig
import mu.KotlinLogging
import okio.IOException
import java.io.File
import kotlin.system.exitProcess

object MayaLauncher {
    private val logger = KotlinLogging.logger { }

    @JvmStatic
    fun main(args: Array<String>) {
        installCoroutinesDebugProbes()

        val configFile = checkConfigFile()
        val config = readConfigFile<MayaConfig>(configFile)
        val hostname = HostnameUtils.getHostname()
        val clusterId = if (config.discord.getClusterIdFromHostname) {
            try {
                hostname.split("-")[1].toInt()
            } catch (_: IndexOutOfBoundsException) {
                logger.error { "Invalid hostname ($hostname)! The hostname must contain '-' followed by a numeric ID." }
                exitProcess(1)
            } catch (_: NumberFormatException) {
                logger.error { "Invalid ID in hostname ($hostname)! The value after '-' must be a number." }
                exitProcess(1)
            }
        } else config.discord.replicaId

        val currentCluster = config.discord.clusters.find { it.id == clusterId }
            ?: run {
                logger.error { "Cluster $hostname ($clusterId) not found in config file." }
                exitProcess(1)
            }

        logger.info { "Starting Maya on Cluster ${currentCluster.id} (${currentCluster.name})" }

        runBlocking {
            MayaInstance(config, currentCluster).start()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> readConfigFile(file: File): T {
        try {
            val json = file.readText()
            return Constants.HOCON.decodeFromString<T>(json)
        } catch (e: IOException) {
            e.printStackTrace()
            exitProcess(1)
        }
    }
}