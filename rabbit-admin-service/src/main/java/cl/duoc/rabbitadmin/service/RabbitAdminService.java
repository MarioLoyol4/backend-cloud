package cl.duoc.rabbitadmin.service;

import cl.duoc.rabbitadmin.config.MessagingProperties;
import cl.duoc.rabbitadmin.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;
    private final MessagingProperties props;

    public void crearCola(QueueRequest r) {
        validarNombre(r.name());
        QueueBuilder b = (r.durable() == null || r.durable())
                ? QueueBuilder.durable(r.name())
                : QueueBuilder.nonDurable(r.name());

        if (hayTexto(r.deadLetterExchange())) b.deadLetterExchange(r.deadLetterExchange());
        if (hayTexto(r.deadLetterRoutingKey())) b.deadLetterRoutingKey(r.deadLetterRoutingKey());
        if (r.ttlMs() != null) b.ttl(r.ttlMs().intValue());
        if (r.maxLength() != null) b.maxLength(r.maxLength());

        amqpAdmin.declareQueue(b.build());
        log.info("Cola creada: {}", r.name());
    }

    public QueueInfoResponse infoCola(String nombre) {
        QueueInformation info = amqpAdmin.getQueueInfo(nombre);
        if (info == null) {
            throw new NoSuchElementException("La cola " + nombre + " no existe");
        }
        return new QueueInfoResponse(nombre, true, info.getMessageCount(), info.getConsumerCount());
    }

    public List<QueueInfoResponse> colasDelSistema() {
        var q = props.queues();
        return Stream.of(q.nota(), q.asistencia(), q.boletinSolicitado(), q.boletinGenerado())
                .flatMap(s -> Stream.of(s.name(), s.name() + props.suffix().retry(), s.name() + props.suffix().dlq()))
                .map(nombre -> {
                    QueueInformation info = amqpAdmin.getQueueInfo(nombre);
                    return info == null ? new QueueInfoResponse(nombre, false, 0, 0)
                            : new QueueInfoResponse(nombre, true, info.getMessageCount(), info.getConsumerCount());
                }).toList();
    }

    public void eliminarCola(String nombre) {
        infoCola(nombre);
        amqpAdmin.deleteQueue(nombre);
        log.info("Cola eliminada: {}", nombre);
    }

    public int purgarCola(String nombre) {
        infoCola(nombre);
        return amqpAdmin.purgeQueue(nombre);
    }

    public void crearExchange(ExchangeRequest r) {
        validarNombre(r.name());
        boolean durable = r.durable() == null || r.durable();
        Exchange exchange = switch (r.type()) {
            case DIRECT -> new DirectExchange(r.name(), durable, false);
            case TOPIC -> new TopicExchange(r.name(), durable, false);
            case FANOUT -> new FanoutExchange(r.name(), durable, false);
            case HEADERS -> new HeadersExchange(r.name(), durable, false);
        };
        amqpAdmin.declareExchange(exchange);
        log.info("Exchange creado: {} ({})", r.name(), r.type());
    }

    public void eliminarExchange(String nombre) {
        validarNombre(nombre);
        amqpAdmin.deleteExchange(nombre);
        log.info("Exchange eliminado: {}", nombre);
    }

    public void crearBinding(BindingRequest r) {
        amqpAdmin.declareBinding(binding(r.queue(), r.exchange(), r.routingKey()));
    }

    public void eliminarBinding(String cola, String exchange, String routingKey) {
        amqpAdmin.removeBinding(binding(cola, exchange, routingKey));
    }

    private Binding binding(String cola, String exchange, String routingKey) {
        return new Binding(cola, Binding.DestinationType.QUEUE, exchange, routingKey, null);
    }

    private void validarNombre(String nombre) {
        if (nombre.startsWith("amq.")) {
            throw new IllegalArgumentException("Los nombres que empiezan con 'amq.' están reservados por RabbitMQ");
        }
    }

    private boolean hayTexto(String s) {
        return s != null && !s.isBlank();
    }
}
