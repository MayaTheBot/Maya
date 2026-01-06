package net.puffinmay.maya.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.puffinmay.maya.MayaInstance
import net.puffinmay.maya.database.dao.ConnectionsEntity
import net.puffinmay.maya.database.dao.ForwardMessage
import net.puffinmay.maya.database.data.FindConnectionsByChannel
import net.puffinmay.maya.database.table.ForwardMessages
import net.puffinmay.maya.processors.MessageProcessor
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MessagesListener(instance: MayaInstance) : ListenerAdapter() {
    private val coroutineScope = CoroutineScope(instance.coroutineDispatcher + SupervisorJob())
    private val logger = KotlinLogging.logger { }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.isWebhookMessage || event.author.isBot) return

        if (event.message.contentRaw == "<@${event.jda.selfUser.id}>") {
            event.message.reply("> 👋 Olá ${event.author.asMention}, me chamo [**Maya**](https://discord.com/oauth2/authorize?client_id=1452863214176960643) e estou aqui para conectar o seu servidor.\n-# - Use `/maya help` para checar todos os meus comandos disponíveis.\n-# - Use `/maya tutorial` para entender como eu funciono.")
                .queue()
            return
        }

        if (event.message.contentRaw.startsWith("!")) return

        coroutineScope.launch {
            try {
                val connections = ConnectionsEntity.FindConnectionsByChannel(event.channel.idLong)

                if (connections.isEmpty()) return@launch

                val connection = connections.firstOrNull() ?: return@launch
                MessageProcessor.process(event, connection)

                logger.info { "Mensagem recebida com sucesso" }
            } catch (e: Exception) {
                logger.error(e) { "Erro ao processar mensagem: ${e.message}" }
            }
        }
    }

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        if (event.message.isWebhookMessage || event.author.isBot) return

        coroutineScope.launch {
            try {
                val connections = ConnectionsEntity.FindConnectionsByChannel(event.channel.idLong)
                if (connections.isEmpty()) return@launch

                val connection = connections.firstOrNull() ?: return@launch
                MessageProcessor.processEdit(event, connection)
            } catch (e: Exception) {
                logger.error(e) { "Erro ao processar edição de mensagem: ${e.message}" }
            }
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        coroutineScope.launch {
            try {
                val forwardedMessages = newSuspendedTransaction {
                    ForwardMessage.find {
                        ForwardMessages.originMessageId eq event.messageId
                    }.toList()
                }

                if (forwardedMessages.isEmpty()) return@launch

                forwardedMessages.forEach { forward ->
                    try {
                        val channel = event.jda.getTextChannelById(forward.forwardedChannelId)
                        channel?.deleteMessageById(forward.forwardedMessageId)?.queue(
                            {
                                logger.info { "Mensagem encaminhada deletada: ${forward.forwardedMessageId}" }
                            },
                            { error ->
                                logger.warn { "Não foi possível deletar mensagem encaminhada: ${error.message}" }
                            }
                        )

                        newSuspendedTransaction {
                            forward.delete()
                        }
                    } catch (e: Exception) {
                        logger.error(e) { "Erro ao deletar mensagem encaminhada: ${e.message}" }
                    }
                }
            } catch (e: Exception) {
                logger.error(e) { "Erro ao processar deleção de mensagem: ${e.message}" }
            }
        }
    }
}