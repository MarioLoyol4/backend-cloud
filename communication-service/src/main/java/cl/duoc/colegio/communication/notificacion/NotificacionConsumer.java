package cl.duoc.colegio.communication.notificacion;

import cl.duoc.colegio.communication.config.MessageAckHandler;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionConsumer {

    private final MessageAckHandler ackHandler;

    @RabbitListener(queues = "${app.messaging.queues.nota.name}")
    public void consumirNota(Message message, Channel channel) throws Exception {
        try {
            String body = new String(message.getBody());
            log.info("Mensaje recibido de RabbitMQ: {}", body);

            // AQUÍ VA TU LÓGICA: Guardar notificación en la BD, enviar correo, etc.
            // Thread.sleep(2000); // Simular tiempo de proceso

            ackHandler.ack(channel, message);
            log.info("Mensaje procesado y confirmado con ACK");

        } catch (Exception e) {
            log.error("Error procesando el mensaje, enviando a DLQ", e);
            ackHandler.nackToDlq(channel, message);
        }
    }
}

