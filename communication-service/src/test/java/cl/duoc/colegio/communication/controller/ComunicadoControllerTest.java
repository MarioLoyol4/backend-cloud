package cl.duoc.colegio.communication.controller;

import cl.duoc.colegio.communication.model.Comunicado;
import cl.duoc.colegio.communication.service.ComunicadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicadoControllerTest {

    @Mock
    private ComunicadoService comunicadoService;

    private ComunicadoController comunicadoController;

    @BeforeEach
    void setUp() {
        comunicadoController = new ComunicadoController(comunicadoService);
    }

    @Test
    @DisplayName("publicar debe publicar un comunicado correctamente")
    void testPublicar() {
        // Arrange
        Comunicado comunicadoNuevo = new Comunicado();
        comunicadoNuevo.setTitulo("Reunión de Apoderados");
        comunicadoNuevo.setContenido("Se cita a reunión para el viernes 15 de mayo a las 18:00 hrs. Asistencia obligatoria.");
        comunicadoNuevo.setAutorId("1");
        comunicadoNuevo.setDestinatario("GENERAL");

        Comunicado comunicadoPublicado = new Comunicado();
        comunicadoPublicado.setId(1L);
        comunicadoPublicado.setTitulo("Reunión de Apoderados");
        comunicadoPublicado.setContenido("Se cita a reunión para el viernes 15 de mayo a las 18:00 hrs. Asistencia obligatoria.");
        comunicadoPublicado.setAutorId("1");
        comunicadoPublicado.setDestinatario("GENERAL");
        comunicadoPublicado.setFechaPublicacion(LocalDateTime.now());

        when(comunicadoService.publicar(any(Comunicado.class))).thenReturn(comunicadoPublicado);

        // Act
        ResponseEntity<Comunicado> respuesta = comunicadoController.publicar(comunicadoNuevo);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getId());
        assertEquals("Reunión de Apoderados", respuesta.getBody().getTitulo());
        verify(comunicadoService, times(1)).publicar(any(Comunicado.class));
    }

    @Test
    @DisplayName("todos debe retornar todos los comunicados")
    void testTodos() {
        // Arrange
        Comunicado comunicado1 = new Comunicado();
        comunicado1.setId(1L);
        comunicado1.setTitulo("Reunión de Apoderados");
        comunicado1.setDestinatario("GENERAL");

        Comunicado comunicado2 = new Comunicado();
        comunicado2.setId(2L);
        comunicado2.setTitulo("Feria Científica");
        comunicado2.setDestinatario("CURSO_1");

        when(comunicadoService.getTodos()).thenReturn(List.of(comunicado1, comunicado2));

        // Act
        ResponseEntity<List<Comunicado>> respuesta = comunicadoController.todos();

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().size());
        assertEquals("Reunión de Apoderados", respuesta.getBody().get(0).getTitulo());
        assertEquals("Feria Científica", respuesta.getBody().get(1).getTitulo());
        verify(comunicadoService, times(1)).getTodos();
    }

    @Test
    @DisplayName("porDestinatario debe retornar comunicados por destinatario")
    void testPorDestinatario() {
        // Arrange
        String destinatario = "GENERAL";
        Comunicado comunicado = new Comunicado();
        comunicado.setId(1L);
        comunicado.setTitulo("Reunión de Apoderados");
        comunicado.setDestinatario(destinatario);

        when(comunicadoService.getByDestinatario(destinatario))
                .thenReturn(List.of(comunicado));

        // Act
        ResponseEntity<List<Comunicado>> respuesta = comunicadoController.porDestinatario(destinatario);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
        assertEquals("GENERAL", respuesta.getBody().get(0).getDestinatario());
        verify(comunicadoService, times(1)).getByDestinatario(destinatario);
    }

    @Test
    @DisplayName("todos debe retornar lista vacía si no hay comunicados")
    void testTodosVacio() {
        // Arrange
        when(comunicadoService.getTodos()).thenReturn(List.of());

        // Act
        ResponseEntity<List<Comunicado>> respuesta = comunicadoController.todos();

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().isEmpty());
        verify(comunicadoService, times(1)).getTodos();
    }

    @Test
    @DisplayName("porDestinatario debe retornar lista vacía si no hay comunicados para ese destinatario")
    void testPorDestinatarioVacio() {
        // Arrange
        String destinatario = "NO_EXISTE";
        when(comunicadoService.getByDestinatario(destinatario)).thenReturn(List.of());

        // Act
        ResponseEntity<List<Comunicado>> respuesta = comunicadoController.porDestinatario(destinatario);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().isEmpty());
        verify(comunicadoService, times(1)).getByDestinatario(destinatario);
    }
}
