package cl.duoc.colegio.academic.messaging.producer;

import cl.duoc.colegio.academic.messaging.config.MessagingProperties;
import cl.duoc.colegio.academic.messaging.event.NotaRegistradaEvent;
import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.model.Evaluacion;
import cl.duoc.colegio.academic.model.Nota;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotaEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties props;

    public void notaRegistrada(Nota nota) {
        try {
            Estudiante e = nota.getEstudiante();
            Evaluacion ev = nota.getEvaluacion();
            var evento = new NotaRegistradaEvent(
                    nota.getId(),
                    e.getId(),
                    e.getNombre() + " " + e.getApellido(),
                    ev.getNombre(),
                    ev.getAsignatura().getNombre(),
                    nota.getValor()
            );
            rabbitTemplate.convertAndSend(props.exchanges().eventos(), props.queues().nota().routingKey(), evento);
        } catch (AmqpException ex) {
            log.warn("No se pudo publicar nota.registrada (la nota ya fue guardada): {}", ex.getMessage());
        }
    }
}
