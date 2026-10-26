package cl.duoc.colegio.communication.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class NotificationTopologyConfig {
    private final MessagingProperties props;

    @Bean
    public TopicExchange eventosExchange() {
        return new TopicExchange(props.getExchanges().getEventos(), true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(props.getExchanges().getDlx(), true, false);
    }

    // Cola de Notas (conectada a la DLQ)
    @Bean
    public Queue notaQueue() {
        return QueueBuilder.durable(props.getQueues().getNota().getName())
                .withArgument("x-dead-letter-exchange", props.getExchanges().getDlx())
                .build();
    }

    @Bean
    public Queue notaDlq() {
        return QueueBuilder.durable(props.getQueues().getNota().getName() + props.getSuffix().getDlq()).build();
    }

    @Bean
    public Binding notaBinding(Queue notaQueue, TopicExchange eventosExchange) {
        return BindingBuilder.bind(notaQueue).to(eventosExchange).with(props.getQueues().getNota().getRoutingKey());
    }

    @Bean
    public Binding dlqBinding(Queue notaDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(notaDlq).to(dlxExchange).with(props.getQueues().getNota().getName());
    }
}



