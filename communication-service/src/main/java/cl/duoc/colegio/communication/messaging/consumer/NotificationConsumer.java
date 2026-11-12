package cl.duoc.colegio.communication.messaging.consumer;

import cl.duoc.colegio.communication.messaging.event.BoletinGeneradoEvent;
import cl.duoc.colegio.communication.messaging.event.NotaRegistradaEvent;
import cl.duoc.colegio.communication.messaging.support.MessageAckHandler;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final MessageAckHandler ackHandler;

    @RabbitListener(queues = "${app.messaging.queues.nota.name}", ackMode = "MANUAL")
    public void escucharNotaRegistrada(Message message, Channel channel) throws IOException {
        ackHandler.handle(message, channel, NotaRegistradaEvent.class, evento -> {
            log.info("==================================================");
            log.info("[NOTIFICACIÓN] Enviando correo por nueva nota registrada");
            log.info("Estudiante: {} (ID: {})", evento.estudianteNombre(), evento.estudianteId());
            log.info("Asignatura: {} | Evaluación: {} | Nota: {}",
                    evento.asignaturaNombre(), evento.evaluacionNombre(), evento.valor());
            log.info("==================================================");
        });
    }

    @RabbitListener(queues = "${app.messaging.queues.boletin-generado.name}", ackMode = "MANUAL")
    public void escucharBoletinGenerado(Message message, Channel channel) throws IOException {
        ackHandler.handle(message, channel, BoletinGeneradoEvent.class, evento -> {
            log.info("==================================================");
            log.info("[NOTIFICACIÓN] Enviando correo con Boletín Académico");
            log.info("Estudiante: {} | Promedio General: {}", evento.estudianteNombre(), evento.promedioGeneral());
            log.info("Detalle de Asignaturas: {}", evento.detalle());
            log.info("==================================================");
        });
    }
}
