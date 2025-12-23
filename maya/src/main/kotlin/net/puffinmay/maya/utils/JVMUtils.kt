package net.puffinmay.maya.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes

@OptIn(ExperimentalCoroutinesApi::class)
fun installCoroutinesDebugProbes() {
    System.setProperty("kotlinx.coroutines.debug", "on")
    System.setProperty("kotlinx.coroutines.stacktrace.recovery", "true")

    DebugProbes.enableCreationStackTraces = false
    DebugProbes.install()
}