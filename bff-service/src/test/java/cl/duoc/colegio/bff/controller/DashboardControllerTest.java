package cl.duoc.colegio.bff.controller;

import cl.duoc.colegio.bff.client.MicroservicioClient;
import cl.duoc.colegio.bff.security.IdentidadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private MicroservicioClient client;

    @Mock
    private IdentidadService identidadService;

    @Mock
    private Jwt jwt;

    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        dashboardController = new DashboardController(client, identidadService);

        ReflectionTestUtils.setField(dashboardController, "academicUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(dashboardController, "attendanceUrl", "http://localhost:8082");
        ReflectionTestUtils.setField(dashboardController, "communicationUrl", "http://localhost:8083");
    }

    @Test
    @DisplayName("obtenerResumenEstudiante debe retornar datos para DOCENTE")
    void testObtenerResumenEstudianteDocente() {
        Long estudianteId = 1L;
        when(jwt.getClaimAsStringList("roles")).thenReturn(List.of("DOCENTE"));

        when(client.llamarSeguro("academic-service", "http://localhost:8081/api/notas/estudiante/1"))
                .thenReturn(Map.of("notas", List.of()));
        when(client.llamarSeguro("attendance-service", "http://localhost:8082/api/anotaciones/estudiante/1"))
                .thenReturn(Map.of("anotaciones", List.of()));
        when(client.llamarSeguro("attendance-service", "http://localhost:8082/api/asistencias/estudiante/1"))
                .thenReturn(Map.of("asistencias", List.of()));
        when(client.llamarSeguro("communication-service", "http://localhost:8083/api/comunicados/destinatario/APODERADOS"))
                .thenReturn(Map.of("comunicados", List.of()));

        ResponseEntity<?> respuesta = dashboardController.obtenerResumenEstudiante(estudianteId, jwt);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(client, times(4)).llamarSeguro(anyString(), anyString());
        verify(identidadService, never()).resolverPerfil(any());
    }

    @Test
    @DisplayName("obtenerResumenEstudiante debe denegar acceso a APODERADO sin permiso")
    void testObtenerResumenEstudianteApoderadoSinAcceso() {
        Long estudianteId = 99L;
        when(jwt.getClaimAsStringList("roles")).thenReturn(List.of("APODERADO"));
        when(identidadService.resolverPerfil(jwt))
                .thenReturn(new IdentidadService.Perfil("10", "APODERADO", List.of(1L, 2L, 3L)));

        ResponseEntity<?> respuesta = dashboardController.obtenerResumenEstudiante(estudianteId, jwt);

        assertEquals(HttpStatus.FORBIDDEN, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().toString().contains("error"));
        verify(client, never()).llamarSeguro(anyString(), anyString());
    }

    @Test
    @DisplayName("obtenerResumenEstudiante debe permitir acceso a APODERADO con permiso")
    void testObtenerResumenEstudianteApoderadoConAcceso() {
        Long estudianteId = 1L;
        when(jwt.getClaimAsStringList("roles")).thenReturn(List.of("APODERADO"));
        when(identidadService.resolverPerfil(jwt))
                .thenReturn(new IdentidadService.Perfil("10", "APODERADO", List.of(1L, 2L, 3L)));

        when(client.llamarSeguro(anyString(), anyString()))
                .thenReturn(Map.of("datos", List.of()));

        ResponseEntity<?> respuesta = dashboardController.obtenerResumenEstudiante(estudianteId, jwt);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(client, times(4)).llamarSeguro(anyString(), anyString());
    }

    @Test
    @DisplayName("miPerfil debe retornar los datos del estudiante actual")
    void testMiPerfil() {
        when(identidadService.resolverPerfil(jwt))
                .thenReturn(new IdentidadService.Perfil("estudiante-1", "ESTUDIANTE", List.of()));

        when(client.llamarSeguro(anyString(), anyString()))
                .thenReturn(Map.of("datos", List.of()));

        ResponseEntity<?> respuesta = dashboardController.miPerfil(jwt);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(client, times(3)).llamarSeguro(anyString(), anyString());
    }

    @Test
    @DisplayName("resumenCurso debe retornar los datos del curso")
    void testResumenCurso() {
        Long cursoId = 1L;

        when(client.llamarSeguro(anyString(), anyString()))
                .thenReturn(Map.of("datos", List.of()));

        ResponseEntity<?> respuesta = dashboardController.resumenCurso(cursoId);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(client, times(2)).llamarSeguro(anyString(), anyString());
    }

    @Test
    @DisplayName("registrarAsistencia debe reenviar la petición al microservicio de asistencia")
    void testRegistrarAsistencia() {
        Map<String, Object> payload = Map.of("estudianteId", 1L, "fecha", "2026-07-14", "estado", "PRESENTE");
        when(client.llamarConCircuitBreaker(eq("attendance-service"), eq("http://localhost:8082/api/asistencias"), any()))
                .thenReturn(Map.of("id", 1));

        ResponseEntity<?> respuesta = dashboardController.registrarAsistencia(payload);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(client, times(1)).llamarConCircuitBreaker(eq("attendance-service"), eq("http://localhost:8082/api/asistencias"), any());
    }

    @Test
    @DisplayName("registrarAnotacion debe reenviar la petición al microservicio de asistencia")
    void testRegistrarAnotacion() {
        Map<String, Object> payload = Map.of("estudianteId", 1L, "tipo", "POSITIVA", "descripcion", "Buen trabajo", "fecha", "2026-07-14");
        when(client.llamarConCircuitBreaker(eq("attendance-service"), eq("http://localhost:8082/api/anotaciones"), any()))
                .thenReturn(Map.of("id", 2));

        ResponseEntity<?> respuesta = dashboardController.registrarAnotacion(payload);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(client, times(1)).llamarConCircuitBreaker(eq("attendance-service"), eq("http://localhost:8082/api/anotaciones"), any());
    }

    @Test
    @DisplayName("publicarComunicado debe reenviar la petición al microservicio de comunicación")
    void testPublicarComunicado() {
        Map<String, Object> payload = Map.of("titulo", "Aviso", "contenido", "Prueba", "autorId", "DOCENTE", "destinatario", "GENERAL");
        when(client.llamarConCircuitBreaker(eq("communication-service"), eq("http://localhost:8083/api/comunicados"), any()))
                .thenReturn(Map.of("id", 3));

        ResponseEntity<?> respuesta = dashboardController.publicarComunicado(payload);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(client, times(1)).llamarConCircuitBreaker(eq("communication-service"), eq("http://localhost:8083/api/comunicados"), any());
    }

    @Test
    @DisplayName("crearEvaluacion debe reenviar la petición al microservicio académico")
    void testCrearEvaluacion() {
        Map<String, Object> payload = Map.of(
                "nombre", "Prueba de Historia",
                "fecha", "2026-07-20",
                "asignatura", Map.of("id", 2L)
        );
        when(client.llamarConCircuitBreaker(eq("academic-service"), eq("http://localhost:8081/api/evaluaciones"), any()))
                .thenReturn(Map.of("id", 7));

        ResponseEntity<?> respuesta = dashboardController.crearEvaluacion(payload);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(client, times(1)).llamarConCircuitBreaker(eq("academic-service"), eq("http://localhost:8081/api/evaluaciones"), any());
    }

    @Test
    @DisplayName("registrarNota debe reenviar la petición al microservicio académico")
    void testRegistrarNota() {
        Map<String, Object> payload = Map.of(
                "valor", 6.5,
                "estudiante", Map.of("id", 1L),
                "evaluacion", Map.of("id", 2L)
        );
        when(client.llamarConCircuitBreaker(eq("academic-service"), eq("http://localhost:8081/api/notas"), any()))
                .thenReturn(Map.of("id", 4));

        ResponseEntity<?> respuesta = dashboardController.registrarNota(payload);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(client, times(1)).llamarConCircuitBreaker(eq("academic-service"), eq("http://localhost:8081/api/notas"), any());
    }
}