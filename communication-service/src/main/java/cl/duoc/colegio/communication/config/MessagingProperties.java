package cl.duoc.colegio.communication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.messaging")
public class MessagingProperties {
    private Exchanges exchanges;
    private Queues queues;
    private Suffix suffix;

    @Data public static class Exchanges { private String eventos; private String documentos; private String retry; private String dlx; }
    @Data public static class Queues { private QueueConfig nota; private QueueConfig asistencia; }
    @Data public static class QueueConfig { private String name; private String routingKey; }
    @Data public static class Suffix { private String retry; private String dlq; }
}

