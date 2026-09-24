package cl.duoc.colegio.bff.controller;

import cl.duoc.colegio.bff.client.MicroservicioClient;
import cl.duoc.colegio.bff.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MicroservicioClient client;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(jwtUtil, client);
        ReflectionTestUtils.setField(authController, "academicUrl", "http://localhost:8081");
    }

    @Test
    @DisplayName("login debe retornar token cuando academic-service valida correctamente")
    void testLoginUsuarioValido() {
        Map<String, String> credenciales = Map.of(
                "rut", "12345678-9",
                "password", "apoderado123"
        );

        Map<String, Object> respuestaAcademic = Map.of(
                "referenciaId", "1",
                "rol", "APODERADO",
                "estudiantesACargo", List.of()
        );

        when(client.llamarConCircuitBreaker(
                "academic-service",
                "http://localhost:8081/api/auth-academic/validar",
                credenciales
        )).thenReturn(respuestaAcademic);

        when(jwtUtil.generarToken("1", "APODERADO", List.of()))
                .thenReturn("token-jwt-apoderado");

        ResponseEntity<?> respuesta = authController.login(credenciales);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().toString().contains("token"));
        verify(client, times(1)).llamarConCircuitBreaker(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("login debe retornar 503 si academic-service falla")
    void testLoginServicioNoDisponible() {
        Map<String, String> credenciales = Map.of(
                "rut", "12345678-9",
                "password", "apoderado123"
        );

        when(client.llamarConCircuitBreaker(
                "academic-service",
                "http://localhost:8081/api/auth-academic/validar",
                credenciales
        )).thenThrow(new RuntimeException("Servicio no disponible"));

        ResponseEntity<?> respuesta = authController.login(credenciales);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().toString().contains("error"));
    }

    @Test
    @DisplayName("login debe retornar 401 si academic-service devuelve respuesta no valida")
    void testLoginRespuestaNoValida() {
        Map<String, String> credenciales = Map.of(
                "rut", "12345678-9",
                "password", "incorrecta"
        );

        when(client.llamarConCircuitBreaker(
                "academic-service",
                "http://localhost:8081/api/auth-academic/validar",
                credenciales
        )).thenReturn("respuesta-invalida");

        ResponseEntity<?> respuesta = authController.login(credenciales);

        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
    }

    @Test
    @DisplayName("login debe generar token con rol ESTUDIANTE correctamente")
    void testLoginEstudiante() {
        Map<String, String> credenciales = Map.of(
                "rut", "99999999-9",
                "password", "estudiante123"
        );

        Map<String, Object> respuestaAcademic = Map.of(
                "referenciaId", "5",
                "rol", "ESTUDIANTE",
                "estudiantesACargo", List.of()
        );

        when(client.llamarConCircuitBreaker(
                "academic-service",
                "http://localhost:8081/api/auth-academic/validar",
                credenciales
        )).thenReturn(respuestaAcademic);

        when(jwtUtil.generarToken("5", "ESTUDIANTE", List.of()))
                .thenReturn("token-jwt-estudiante");

        ResponseEntity<?> respuesta = authController.login(credenciales);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(jwtUtil, times(1)).generarToken("5", "ESTUDIANTE", List.of());
    }
}