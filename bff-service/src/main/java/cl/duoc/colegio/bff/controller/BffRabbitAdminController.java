package cl.duoc.colegio.bff.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/bff/rabbit")
@PreAuthorize("hasAuthority('APPROLE_ADMIN')")
@RequiredArgsConstructor
public class BffRabbitAdminController {

    @Value("${services.rabbit-admin.url:http://localhost:8084}")
    private String rabbitAdminUrl;

    @Value("${internal.api.key:secret-key-colegio}")
    private String internalApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
    public ResponseEntity<Object> proxyRabbitAdmin(HttpServletRequest request, @RequestBody(required = false) Object body) {
        // Redirige las peticiones desde /api/bff/rabbit/* hacia /api/rabbit/* en el puerto 8084
        String path = request.getRequestURI().replace("/api/bff/rabbit", "/api/rabbit");
        String url = rabbitAdminUrl + path;

        // Inyecta el encabezado de seguridad interna requerido por rabbit-admin-service
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Api-Key", internalApiKey);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        return restTemplate.exchange(url, method, entity, Object.class);
    }
}