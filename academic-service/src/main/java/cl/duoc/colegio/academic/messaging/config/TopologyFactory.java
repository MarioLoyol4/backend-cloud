package cl.duoc.colegio.academic.messaging.config;

import org.springframework.amqp.core.*;

public final class TopologyFactory {
    private TopologyFactory() {}

    public static Declarables conReintentoYDlq(
            MessagingProperties p,
            MessagingProperties.QueueSpec spec,
            Exchange principal,
            DirectExchange retry,
            DirectExchange dlx
    ) {
        String nombre = spec.name();
        String nombreRetry = nombre + p.suffix().retry();
        String nombreDlq = nombre + p.suffix().dlq();

        Queue cola = QueueBuilder.durable(nombre)
                .deadLetterExchange(dlx.getName())
                .deadLetterRoutingKey(nombreDlq)
                .build();

        Queue espera = QueueBuilder.durable(nombreRetry)
                .ttl((int) p.retry().delayMs())
                .deadLetterExchange(principal.getName())
                .deadLetterRoutingKey(spec.routingKey())
                .build();

        Queue dlq = QueueBuilder.durable(nombreDlq).build();

        return new Declarables(
                cola, espera, dlq,
                BindingBuilder.bind(cola).to(principal).with(spec.routingKey()).noargs(),
                BindingBuilder.bind(espera).to(retry).with(nombreRetry),
                BindingBuilder.bind(dlq).to(dlx).with(nombreDlq)
        );
    }
}
