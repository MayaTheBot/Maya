package net.puffinmay.maya.utils

import net.puffinmay.maya.MayaInstance
import java.time.LocalTime

object TasksUtils {
    fun launchTasks(instance: MayaInstance) {}

    private fun at(hour: Int, minute: Int) = LocalTime.of(hour, minute)
}