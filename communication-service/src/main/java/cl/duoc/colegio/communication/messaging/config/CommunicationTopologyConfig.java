package cl.duoc.colegio.communication.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommunicationTopologyConfig {

    @Bean
    public TopicExchange eventosExchange(MessagingProperties props) {
        return new TopicExchange(props.exchanges().eventos(), true, false);
    }

    @Bean
    public TopicExchange retryExchange(MessagingProperties props) {
        return new TopicExchange(props.exchanges().retry(), true, false);
    }

    @Bean
    public TopicExchange dlxExchange(MessagingProperties props) {
        return new TopicExchange(props.exchanges().dlx(), true, false);
    }

    @Bean
    public Queue boletinGeneradoQueue(MessagingProperties props) {
        return QueueBuilder.durable(props.queues().boletinGenerado().name()).build();
    }

    @Bean
    public Queue boletinGeneradoRetryQueue(MessagingProperties props) {
        return QueueBuilder.durable(props.queues().boletinGenerado().name() + props.suffix().retry())
                .deadLetterExchange(props.exchanges().eventos())
                .deadLetterRoutingKey(props.queues().boletinGenerado().routingKey())
                .ttl((int) props.retry().delayMs())
                .build();
    }

    @Bean
    public Queue boletinGeneradoDlq(MessagingProperties props) {
        return QueueBuilder.durable(props.queues().boletinGenerado().name() + props.suffix().dlq()).build();
    }

    @Bean
    public Binding boletinGeneradoBinding(Queue boletinGeneradoQueue, TopicExchange eventosExchange, MessagingProperties props) {
        return BindingBuilder.bind(boletinGeneradoQueue).to(eventosExchange).with(props.queues().boletinGenerado().routingKey());
    }

    @Bean
    public Binding boletinGeneradoRetryBinding(Queue boletinGeneradoRetryQueue, TopicExchange retryExchange, MessagingProperties props) {
        return BindingBuilder.bind(boletinGeneradoRetryQueue).to(retryExchange).with(props.queues().boletinGenerado().name() + props.suffix().retry());
    }

    @Bean
    public Binding boletinGeneradoDlqBinding(Queue boletinGeneradoDlq, TopicExchange dlxExchange, MessagingProperties props) {
        return BindingBuilder.bind(boletinGeneradoDlq).to(dlxExchange).with(props.queues().boletinGenerado().name());
    }

    @Bean
    public Queue notaQueue(MessagingProperties props) {
        return QueueBuilder.durable(props.queues().nota().name()).build();
    }

    @Bean
    public Queue notaRetryQueue(MessagingProperties props) {
        return QueueBuilder.durable(props.queues().nota().name() + props.suffix().retry())
                .deadLetterExchange(props.exchanges().eventos())
                .deadLetterRoutingKey(props.queues().nota().routingKey())
                .ttl((int) props.retry().delayMs())
                .build();
    }

    @Bean
    public Queue notaDlq(MessagingProperties props) {
        return QueueBuilder.durable(props.queues().nota().name() + props.suffix().dlq()).build();
    }

    @Bean
    public Binding notaBinding(Queue notaQueue, TopicExchange eventosExchange, MessagingProperties props) {
        return BindingBuilder.bind(notaQueue).to(eventosExchange).with(props.queues().nota().routingKey());
    }

    @Bean
    public Binding notaRetryBinding(Queue notaRetryQueue, TopicExchange retryExchange, MessagingProperties props) {
        return BindingBuilder.bind(notaRetryQueue).to(retryExchange).with(props.queues().nota().name() + props.suffix().retry());
    }

    @Bean
    public Binding notaDlqBinding(Queue notaDlq, TopicExchange dlxExchange, MessagingProperties props) {
        return BindingBuilder.bind(notaDlq).to(dlxExchange).with(props.queues().nota().name());
    }
}
