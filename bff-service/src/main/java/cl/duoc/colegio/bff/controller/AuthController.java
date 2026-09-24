package cl.duoc.colegio.bff.controller;

import cl.duoc.colegio.bff.client.MicroservicioClient;
import cl.duoc.colegio.bff.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String ERROR_KEY = "error";
    private static final String MSG_ERROR_CREDENCIALES = "Credenciales incorrectas";
    private static final String ROL_ADMIN = "ADMIN";


    private final JwtUtil jwtUtil;
    private final MicroservicioClient client;

    @Value("${services.academic.url}")
    private String academicUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String rut = credenciales.get("rut");
        String password = credenciales.get("password");



        // Resto de usuarios — consultar academic-service
        try {
            Object respuesta = client.llamarConCircuitBreaker(
                    "academic-service",
                    academicUrl + "/api/auth-academic/validar",
                    credenciales  // enviamos rut + password
            );

            if (respuesta instanceof Map<?, ?> datos) {
                String rol = (String) datos.get("rol");
                String referenciaId = (String) datos.get("referenciaId");

                @SuppressWarnings("unchecked")
                List<Long> estudiantesACargo = (List<Long>) datos.get("estudiantesACargo");

                return ResponseEntity.ok(Map.of(
                        "token", jwtUtil.generarToken(referenciaId, rol, estudiantesACargo),
                        "rol", rol
                ));
            }

            return ResponseEntity.status(401).body(Map.of(ERROR_KEY, MSG_ERROR_CREDENCIALES));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR " +e.getMessage());
            return ResponseEntity.status(503).body(Map.of(ERROR_KEY, "Servicio no disponible"));
        }
    }
}