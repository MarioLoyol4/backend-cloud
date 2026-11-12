package cl.duoc.colegio.academic.messaging.producer;

import cl.duoc.colegio.academic.messaging.config.MessagingProperties;
import cl.duoc.colegio.academic.messaging.event.BoletinGeneradoEvent;
import cl.duoc.colegio.academic.messaging.event.BoletinSolicitadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoletinEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties props;

    public void solicitado(BoletinSolicitadoEvent evento) {
        rabbitTemplate.convertAndSend(props.exchanges().documentos(), props.queues().boletinSolicitado().routingKey(), evento);
    }

    public void generado(BoletinGeneradoEvent evento) {
        rabbitTemplate.convertAndSend(props.exchanges().eventos(), props.queues().boletinGenerado().routingKey(), evento);
    }
}
