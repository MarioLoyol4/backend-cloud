package cl.duoc.colegio.communication.messaging.consumer;

import cl.duoc.colegio.communication.messaging.event.*;
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
        ackHandler.handle(message, channel, NotaRegistradaEvent.class, evento ->
                log.info("[NOTIFICACIÓN] Nueva nota: estudiante {} (ID {}), {} / {}, nota {}",
                        evento.estudianteNombre(), evento.estudianteId(),
                        evento.asignatura(), evento.evaluacion(), evento.valor()));
    }

    @RabbitListener(queues = "${app.messaging.queues.asistencia.name}", ackMode = "MANUAL")
    public void escucharAusencia(Message message, Channel channel) throws IOException {
        ackHandler.handle(message, channel, AsistenciaAusenteEvent.class, evento ->
                log.info("[NOTIFICACIÓN] Estudiante {} ausente el {}",
                        evento.estudianteId(), evento.fecha()));
    }

    @RabbitListener(queues = "${app.messaging.queues.evaluacion.name}", ackMode = "MANUAL")
    public void escucharEvaluacionCreada(Message message, Channel channel) throws IOException {
        ackHandler.handle(message, channel, EvaluacionCreadaEvent.class, evento ->
                log.info("[NOTIFICACIÓN] Nueva evaluación {} en {}",
                        evento.nombre(), evento.asignatura()));
    }

    @RabbitListener(queues = "${app.messaging.queues.boletin-generado.name}", ackMode = "MANUAL")
    public void escucharBoletinGenerado(Message message, Channel channel) throws IOException {
        ackHandler.handle(message, channel, BoletinGeneradoEvent.class, evento ->
                log.info("[NOTIFICACIÓN] Boletín de {}: promedio general {}, detalle {}",
                        evento.estudianteNombre(), evento.promedioGeneral(), evento.detalle()));
    }
}