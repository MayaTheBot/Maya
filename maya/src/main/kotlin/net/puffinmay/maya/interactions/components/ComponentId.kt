package net.puffinmay.maya.interactions.components

import java.util.UUID

class ComponentId(val uniqueId: UUID) {
    companion object {
        const val prefix = "maya"

        operator fun invoke(componentWithPrefix: String): ComponentId {
            require(componentWithPrefix.startsWith("$prefix:")) { "It's not mine." }
            return ComponentId(UUID.fromString(componentWithPrefix.substringAfter(":")))
        }
    }

    override fun toString() = "$prefix:$uniqueId"
}