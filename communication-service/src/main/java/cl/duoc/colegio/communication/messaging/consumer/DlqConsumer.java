package cl.duoc.colegio.communication.messaging.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class DlqConsumer {

    @RabbitListener(queues = {
            "${app.messaging.queues.nota.name}.dlq",
            "${app.messaging.queues.asistencia.name}.dlq",
            "${app.messaging.queues.evaluacion.name}.dlq"
    })
    public void registrar(Message message) {
        log.error("[DLQ] mensaje no entregado en {}: {}",
                message.getMessageProperties().getConsumerQueue(),
                new String(message.getBody(), StandardCharsets.UTF_8));
    }
}