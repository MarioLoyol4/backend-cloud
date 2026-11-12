package cl.duoc.colegio.academic.messaging.consumer;

import cl.duoc.colegio.academic.messaging.event.BoletinGeneradoEvent;
import cl.duoc.colegio.academic.messaging.event.BoletinSolicitadoEvent;
import cl.duoc.colegio.academic.messaging.producer.BoletinEventPublisher;
import cl.duoc.colegio.academic.messaging.support.MessageAckHandler;
import cl.duoc.colegio.academic.service.BoletinService;
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
public class BoletinConsumer {
    private final MessageAckHandler ackHandler;
    private final BoletinService boletinService;
    private final BoletinEventPublisher eventPublisher;

    @RabbitListener(queues = "${app.messaging.queues.boletin-solicitado.name}")
    public void consumirSolicitudBoletin(Message message, Channel channel) throws IOException {
        ackHandler.handle(message, channel, BoletinSolicitadoEvent.class, evento -> {
            log.info("Procesando solicitud de boletín para estudiante ID: {}", evento.estudianteId());
            BoletinGeneradoEvent generado = boletinService.generar(evento.estudianteId());
            eventPublisher.generado(generado);
        });
    }
}