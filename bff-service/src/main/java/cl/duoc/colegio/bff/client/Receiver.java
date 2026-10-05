package cl.duoc.colegio.bff.client;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class Receiver {

    @RabbitListener(queues = "hello")
    public void receiveMessage(String message) {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            System.out.println(
                    "[" + timestamp + "] [ ] Mensaje recibido: '" + message + "'"
            );
            // Aquí va tu lógica de procesamiento del mensaje[cite: 42]
            // Si lanzas una excepción, Spring AMQP reintentará el mensaje[cite: 42]
            // según la configuración de retry[cite: 42]
        } catch (Exception e) {
            System.err.println("[ ] Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
            // Relanzar la excepción para que Spring AMQP maneje el retry[cite: 42]
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "hello")
    public void receiveMessageAdvanced(String message, Message rawMessage, Channel channel) throws Exception {
        try {
            System.out.println("[ ] Mensaje recibido: '" + message + "'");
            System.out.println(" - Content-Type: " + rawMessage.getMessageProperties().getContentType());
            System.out.println(" - Timestamp: " + rawMessage.getMessageProperties().getTimestamp());

            // Confirmación manual del mensaje (si usas manual acks)[cite: 43]
            channel.basicAck(rawMessage.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.err.println("[ ] Error: " + e.getMessage());
            // Rechazar el mensaje y requearlo (si usas manual acks)[cite: 43]
            channel.basicNack(rawMessage.getMessageProperties().getDeliveryTag(), false, true);
            throw e;
        }
    }

}