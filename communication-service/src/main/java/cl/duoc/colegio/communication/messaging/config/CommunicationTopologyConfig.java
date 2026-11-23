package cl.duoc.colegio.communication.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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
    public Declarables colasDeNegocio(MessagingProperties p, TopicExchange eventosExchange,
                                      TopicExchange retryExchange, TopicExchange dlxExchange) {
        List<Declarable> declarables = new ArrayList<>();
        List<MessagingProperties.QueueConfig> colas = List.of(
                p.queues().boletinGenerado(), p.queues().nota(),
                p.queues().asistencia(), p.queues().evaluacion()
        );

        for (MessagingProperties.QueueConfig q : colas) {
            Queue principal = QueueBuilder.durable(q.name())
                    .deadLetterExchange(p.exchanges().dlx())
                    .deadLetterRoutingKey(q.name())
                    .build();

            Queue retry = QueueBuilder.durable(q.name() + p.suffix().retry())
                    .deadLetterExchange(p.exchanges().eventos())
                    .deadLetterRoutingKey(q.routingKey())
                    .ttl((int) p.retry().delayMs())
                    .build();

            Queue dlq = QueueBuilder.durable(q.name() + p.suffix().dlq()).build();

            declarables.add(principal);
            declarables.add(retry);
            declarables.add(dlq);
            declarables.add(BindingBuilder.bind(principal).to(eventosExchange).with(q.routingKey()));
            declarables.add(BindingBuilder.bind(retry).to(retryExchange).with(q.name() + p.suffix().retry()));
            declarables.add(BindingBuilder.bind(dlq).to(dlxExchange).with(q.name()));
        }
        return new Declarables(declarables);
    }
}