package cl.duoc.colegio.communication.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class MessageAckHandler {
    public void ack(Channel channel, Message message) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    public void nackToDlq(Channel channel, Message message) throws IOException {
        // requeue=false envía el mensaje a la DLX
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
    }
}

