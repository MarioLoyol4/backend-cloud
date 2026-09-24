package cl.duoc.colegio.bff.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class MicroservicioClient {

    private final RestTemplate restTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    private static final String MENSAJE_ERROR_SERVICIO = "Servicio no disponible temporalmente, intente mas tarde por favor";

    @Value("${internal.api.key}")
    private String internalApiKey;

    public MicroservicioClient(RestTemplate restTemplate,
                               CircuitBreakerRegistry circuitBreakerRegistry) {
        this.restTemplate = restTemplate;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Object llamarConCircuitBreaker(String nombreServicio, String url) {
        var cb = circuitBreakerRegistry.circuitBreaker(nombreServicio);
        return cb.executeSupplier(() -> {
            HttpEntity<?> entity = new HttpEntity<>(headers());
            return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
        });
    }

    public Object llamarConCircuitBreaker(String nombreServicio, String url, Object body) {
        var cb = circuitBreakerRegistry.circuitBreaker(nombreServicio);
        return cb.executeSupplier(() -> {
            HttpEntity<?> entity = new HttpEntity<>(body, headers());
            return restTemplate.exchange(url, HttpMethod.POST, entity, Object.class).getBody();
        });
    }

    public Object llamarSeguro(String nombreServicio, String url) {
        try {
            return llamarConCircuitBreaker(nombreServicio, url);
        } catch (CallNotPermittedException e) {
            return Map.of(
                    "disponible", false,
                    "servicio", nombreServicio,
                    "mensaje", "Servicio temporalmente no disponible"
            );
        } catch (Exception e) {
            return Map.of(
                    "disponible", false,
                    "servicio", nombreServicio,
                    "mensaje",  MENSAJE_ERROR_SERVICIO
            );
        }
    }
}