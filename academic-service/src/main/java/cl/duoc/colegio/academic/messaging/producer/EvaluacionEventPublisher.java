package cl.duoc.colegio.academic.messaging.producer;

import cl.duoc.colegio.academic.messaging.event.EvaluacionCreadaEvent;
import cl.duoc.colegio.academic.model.Evaluacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EvaluacionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.messaging.exchanges.eventos}")
    private String exchange;

    @Value("${app.messaging.queues.evaluacion.routing-key:evaluacion.creada}")
    private String routingKey;

    public void evaluacionCreada(Evaluacion ev) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey,
                    new EvaluacionCreadaEvent(ev.getId(), ev.getNombre(), ev.getAsignatura().getNombre()));
        } catch (AmqpException ex) {
            log.warn("No se pudo publicar evaluacion.creada (la evaluación ya fue guardada): {}", ex.getMessage());
        }
    }
}