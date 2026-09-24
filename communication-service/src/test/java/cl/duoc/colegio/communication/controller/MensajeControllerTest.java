package cl.duoc.colegio.communication.controller;

import cl.duoc.colegio.communication.model.Mensaje;
import cl.duoc.colegio.communication.service.MensajeService;
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
class MensajeControllerTest {
    @Mock
    private MensajeService mensajeService;

    private MensajeController mensajeController;

    @BeforeEach
    void setUp() {
        mensajeController = new MensajeController(mensajeService);
    }

    @Test
    @DisplayName("enviar debe enviar un mensaje correctamente")
    void testEnviar() {
        // Arrange
        Mensaje mensajeNuevo = new Mensaje();
        mensajeNuevo.setRemitenteId("1");
        mensajeNuevo.setDestinatarioId("1");
        mensajeNuevo.setContenido("Estimada apoderada, le informo que Martina tuvo un excelente rendimiento esta semana.");
        mensajeNuevo.setTipoNotificacion("ACADEMICA");
        mensajeNuevo.setFechaEnvio(LocalDateTime.now());

        Mensaje mensajeEnviado = new Mensaje();
        mensajeEnviado.setId(1L);
        mensajeEnviado.setRemitenteId("1");
        mensajeEnviado.setDestinatarioId("1");
        mensajeEnviado.setContenido("Estimada apoderada, le informo que Martina tuvo un excelente rendimiento esta semana.");
        mensajeEnviado.setTipoNotificacion("ACADEMICA");
        mensajeEnviado.setFechaEnvio(LocalDateTime.of(2026, 5, 11, 9, 0));

        when(mensajeService.enviarMensaje(any(Mensaje.class))).thenReturn(mensajeEnviado);

        // Act
        ResponseEntity<Mensaje> respuesta = mensajeController.enviar(mensajeNuevo);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getId());
        verify(mensajeService, times(1)).enviarMensaje(any(Mensaje.class));
    }

    @Test
    @DisplayName("recibidos debe retornar mensajes recibidos por destinatario")
    void testRecibidos() {
        // Arrange
        String destinatarioId = "1";
        Mensaje mensaje1 = new Mensaje();
        mensaje1.setId(1L);
        mensaje1.setDestinatarioId(destinatarioId);
        mensaje1.setContenido("Mensaje 1");

        Mensaje mensaje2 = new Mensaje();
        mensaje2.setId(2L);
        mensaje2.setDestinatarioId(destinatarioId);
        mensaje2.setContenido("Mensaje 2");

        when(mensajeService.getMensajesRecibidos(destinatarioId))
                .thenReturn(List.of(mensaje1, mensaje2));

        // Act
        ResponseEntity<List<Mensaje>> respuesta = mensajeController.recibidos(destinatarioId);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().size());
        verify(mensajeService, times(1)).getMensajesRecibidos(destinatarioId);
    }

    @Test
    @DisplayName("enviados debe retornar mensajes enviados por remitente")
    void testEnviados() {
        // Arrange
        String remitenteId = "1";
        Mensaje mensaje = new Mensaje();
        mensaje.setId(1L);
        mensaje.setRemitenteId(remitenteId);
        mensaje.setContenido("Recuerde enviar la autorización para la salida a terreno.");

        when(mensajeService.getMensajesEnviados(remitenteId))
                .thenReturn(List.of(mensaje));

        // Act
        ResponseEntity<List<Mensaje>> respuesta = mensajeController.enviados(remitenteId);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(remitenteId, respuesta.getBody().get(0).getRemitenteId());
        verify(mensajeService, times(1)).getMensajesEnviados(remitenteId);
    }

    @Test
    @DisplayName("recibidos debe retornar lista vacía si no hay mensajes")
    void testRecibidosVacio() {
        // Arrange
        String destinatarioId = "99";
        when(mensajeService.getMensajesRecibidos(destinatarioId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<Mensaje>> respuesta = mensajeController.recibidos(destinatarioId);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().isEmpty());
        verify(mensajeService, times(1)).getMensajesRecibidos(destinatarioId);
    }
}

