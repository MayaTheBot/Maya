package net.puffinmay.maya.interactions.commands

data class CommandUnleashed(
    val executor: UnleashedCommandExecutor?,
    val command: CommandDeclarationBuilder
)