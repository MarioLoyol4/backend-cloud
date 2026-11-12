package cl.duoc.colegio.attendance.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(Exchanges exchanges, Queues queues) {
    public record Exchanges(String eventos) {}
    public record QueueSpec(String name, String routingKey) {}
    public record Queues(QueueSpec asistencia) {}
}