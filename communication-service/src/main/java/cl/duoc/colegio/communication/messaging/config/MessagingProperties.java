package cl.duoc.colegio.communication.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(
        Exchanges exchanges,
        Queues queues,
        Suffix suffix,
        Retry retry
) {
    public record Exchanges(String eventos, String documentos, String retry, String dlx) {}
    public record Queues(QueueConfig boletinGenerado, QueueConfig nota) {}
    public record QueueConfig(String name, String routingKey) {}
    public record Suffix(String retry, String dlq) {}
    public record Retry(int maxAttempts, long delayMs) {}
}
