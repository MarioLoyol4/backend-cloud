package cl.duoc.colegio.academic.messaging.support;

import cl.duoc.colegio.academic.messaging.config.MessagingProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageAckHandler {
    public static final String RETRY_HEADER = "x-retry-count";
    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties props;
    private final ObjectMapper objectMapper;

    @FunctionalInterface
    public interface MessageAction<T> {
        void ejecutar(T payload) throws Exception;
    }

    public <T> void handle(Message message, Channel channel, Class<T> tipo, MessageAction<T> accion) throws IOException {
        MessageProperties mp = message.getMessageProperties();
        long tag = mp.getDeliveryTag();
        String cola = mp.getConsumerQueue();

        try {
            accion.ejecutar(leer(message, tipo));
        } catch (IllegalArgumentException e) {
            log.error("[{}] mensaje NO recuperable, a DLQ: {}", cola, e.getMessage());
            channel.basicNack(tag, false, false);
            return;
        } catch (Exception e) {
            reintentarDescartar(message, channel, cola, e);
            return;
        }
        channel.basicAck(tag, false);
    }

    private void reintentarDescartar(Message message, Channel channel, String cola, Exception causa) throws IOException {
        MessageProperties mp = message.getMessageProperties();
        int intentosHechos = intentosPrevios(message) + 1;

        if (intentosHechos < props.retry().maxAttempts()) {
            mp.setHeader(RETRY_HEADER, intentosHechos);
            rabbitTemplate.send(props.exchanges().retry(), cola + props.suffix().retry(), message);
            channel.basicAck(mp.getDeliveryTag(), false);
            log.warn("[{}] fallo recuperable ({}/{}), reintento en {} ms: {}", cola, intentosHechos, props.retry().maxAttempts(), props.retry().delayMs(), causa.getMessage());
        } else {
            log.error("[{}] intentos agotados ({}), a DLQ. Último error: {}", cola, intentosHechos, causa.getMessage(), causa);
            channel.basicNack(mp.getDeliveryTag(), false, false);
        }
    }

    private int intentosPrevios(Message message) {
        Object h = message.getMessageProperties().getHeader(RETRY_HEADER);
        return h instanceof Number n ? n.intValue() : 0;
    }

    private <T> T leer(Message message, Class<T> tipo) {
        try {
            return objectMapper.readValue(message.getBody(), tipo);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON inválido: " + e.getMessage(), e);
        }
    }
}
