package cl.duoc.colegio.bff.security;


import cl.duoc.colegio.bff.client.MicroservicioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IdentidadService {

    private final MicroservicioClient client;

    @Value("${services.academic.url}")
    private String academicUrl;

    public record Perfil(String referenciaId, String rol, List<Long> estudiantesACargo) {}

    public String extraerEmail(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) return email;

        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (preferredUsername != null && !preferredUsername.isBlank()) return preferredUsername;

        // Tokens v1.0 (emitidos por sts.windows.net) no traen email/preferred_username,
        // pero sí upn y unique_name con el mismo valor (el UPN del usuario en Entra ID).
        String upn = jwt.getClaimAsString("upn");
        if (upn != null && !upn.isBlank()) return upn;

        return jwt.getClaimAsString("unique_name");
    }

    @SuppressWarnings("unchecked")
    public Perfil resolverPerfil(Jwt jwt) {
        String email = extraerEmail(jwt);
        Object respuesta = client.llamarConCircuitBreaker(
                "academic-service", academicUrl + "/api/auth-academic/perfil?email=" + email);

        if (respuesta instanceof Map<?, ?> datos) {
            return new Perfil(
                    (String) datos.get("referenciaId"),
                    (String) datos.get("rol"),
                    datos.get("estudiantesACargo") != null
                            ? (List<Long>) datos.get("estudiantesACargo")
                            : List.of()
            );
        }
        throw new IllegalStateException("No se pudo resolver el perfil académico para " + email);
    }
}
