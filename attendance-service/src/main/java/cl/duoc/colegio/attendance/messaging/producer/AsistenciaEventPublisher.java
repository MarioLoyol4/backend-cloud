package cl.duoc.colegio.attendance.messaging.producer;

import cl.duoc.colegio.attendance.messaging.event.AsistenciaAusenteEvent;
import cl.duoc.colegio.attendance.messaging.config.MessagingProperties;
import cl.duoc.colegio.attendance.model.Asistencia; // Ajusta según tu paquete de modelo
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsistenciaEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties props;

    public void asistenciaRegistrada(Asistencia a) {
        if (!"AUSENTE".equalsIgnoreCase(a.getEstado())) {
            return; // Solo se notifica si estuvo AUSENTE
        }
        try {
            rabbitTemplate.convertAndSend(props.exchanges().eventos(),
                    props.queues().asistencia().routingKey(),
                    new AsistenciaAusenteEvent(a.getEstudianteId(), String.valueOf(a.getFecha())));
        } catch (AmqpException ex) {
            log.warn("No se pudo publicar asistencia.ausente (la asistencia ya fue guardada): {}", ex.getMessage());
        }
    }
}