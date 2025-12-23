package net.puffinmay.maya.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.puffinmay.maya.MayaInstance
import net.puffinmay.maya.utils.common.Constants
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class GuildsListener(private val instance: MayaInstance) : ListenerAdapter() {
    private val logger = KotlinLogging.logger {}
    private val coroutineScope = CoroutineScope(instance.coroutineDispatcher + SupervisorJob())

    override fun onReady(event: ReadyEvent) {
        coroutineScope.launch {
            event.jda.presence.activity = Activity.customStatus(
                Constants.setMayaActivity(
                    "☄️ Coming Soon 2026...",
                    instance.config.environment,
                    instance.currentCluster.id,
                    event.jda.shardManager?.shards?.size ?: 1
                )
            )
        }
    }
}