package net.puffinmay.maya.interactions.commands

abstract class UnleashedCommandExecutor {
    abstract suspend fun execute(context: CommandContext)
}