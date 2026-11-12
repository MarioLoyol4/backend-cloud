package cl.duoc.colegio.academic.messaging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DocumentosTopologyConfig {
    private final MessagingProperties p;

    @Bean public TopicExchange eventosExchange() { return new TopicExchange(p.exchanges().eventos(), true, false); }
    @Bean public DirectExchange documentosExchange() { return new DirectExchange(p.exchanges().documentos(), true, false); }
    @Bean public DirectExchange retryExchange() { return new DirectExchange(p.exchanges().retry(), true, false); }
    @Bean public DirectExchange dlxExchange() { return new DirectExchange(p.exchanges().dlx(), true, false); }

    @Bean public Declarables boletinSolicitadoTopology() {
        return TopologyFactory.conReintentoYDlq(p, p.queues().boletinSolicitado(), documentosExchange(), retryExchange(), dlxExchange());
    }
}
