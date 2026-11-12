package cl.duoc.rabbitadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(Exchanges exchanges, Queues queues, Suffix suffix, Retry retry) {
    public record Exchanges(String eventos, String documentos, String retry, String dlx) {}
    public record QueueSpec(String name, String routingKey) {}
    public record Queues(QueueSpec nota, QueueSpec asistencia, QueueSpec boletinSolicitado, QueueSpec boletinGenerado) {}
    public record Suffix(String retry, String dlq) {}
    public record Retry(int maxAttempts, long delayMs) {}
}
