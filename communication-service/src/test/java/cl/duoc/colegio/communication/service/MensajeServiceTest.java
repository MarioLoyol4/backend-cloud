package cl.duoc.colegio.communication.service;

import cl.duoc.colegio.communication.model.Mensaje;
import cl.duoc.colegio.communication.notificacion.Notificacion;
import cl.duoc.colegio.communication.notificacion.NotificacionFactory;
import cl.duoc.colegio.communication.repository.MensajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private NotificacionFactory notificacionFactory;

    @Mock
    private Notificacion notificacionMock;

    private MensajeService mensajeService;

    @BeforeEach
    void setUp() {
        mensajeService = new MensajeService(mensajeRepository, notificacionFactory);
    }

    @Test
    @DisplayName("enviarMensaje debe usar el Factory para crear la notificacion correcta y guardar el mensaje")
    void testEnviarMensaje() {
        // Arrange
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitenteId("docente-1");
        mensaje.setDestinatarioId("apoderado-5");
        mensaje.setContenido("Reunion de apoderados");
        mensaje.setTipoNotificacion("EMAIL");

        when(notificacionFactory.crear("EMAIL")).thenReturn(notificacionMock);
        when(mensajeRepository.save(mensaje)).thenReturn(mensaje);

        // Act
        Mensaje resultado = mensajeService.enviarMensaje(mensaje);

        // Assert
        assertNotNull(resultado);
        assertEquals("apoderado-5", resultado.getDestinatarioId());

        // Verifica que el Factory fue llamado con el tipo correcto
        verify(notificacionFactory, times(1)).crear("EMAIL");

        // Verifica que la notificacion fue enviada
        verify(notificacionMock, times(1)).enviar("apoderado-5", "Reunion de apoderados");

        // Verifica que el mensaje fue guardado en el repositorio
        verify(mensajeRepository, times(1)).save(mensaje);
    }

    @Test
    @DisplayName("getMensajesRecibidos debe retornar los mensajes del destinatario")
    void testGetMensajesRecibidos() {
        // Arrange
        Mensaje mensaje1 = new Mensaje();
        mensaje1.setDestinatarioId("apoderado-5");
        mensaje1.setContenido("Mensaje 1");

        Mensaje mensaje2 = new Mensaje();
        mensaje2.setDestinatarioId("apoderado-5");
        mensaje2.setContenido("Mensaje 2");

        when(mensajeRepository.findByDestinatarioId("apoderado-5"))
                .thenReturn(List.of(mensaje1, mensaje2));

        // Act
        List<Mensaje> resultado = mensajeService.getMensajesRecibidos("apoderado-5");

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Mensaje 1", resultado.get(0).getContenido());
        verify(mensajeRepository, times(1)).findByDestinatarioId("apoderado-5");
    }

    @Test
    @DisplayName("getMensajesEnviados debe retornar los mensajes del remitente")
    void testGetMensajesEnviados() {
        // Arrange
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitenteId("docente-1");
        mensaje.setContenido("Mensaje enviado");

        when(mensajeRepository.findByRemitenteId("docente-1"))
                .thenReturn(List.of(mensaje));

        // Act
        List<Mensaje> resultado = mensajeService.getMensajesEnviados("docente-1");

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("docente-1", resultado.get(0).getRemitenteId());
        verify(mensajeRepository, times(1)).findByRemitenteId("docente-1");
    }

    @Test
    @DisplayName("enviarMensaje debe lanzar excepcion si el tipo de notificacion no es valido")
    void testEnviarMensajeTipoInvalido() {
        // Arrange
        Mensaje mensaje = new Mensaje();
        mensaje.setTipoNotificacion("SMS");

        when(notificacionFactory.crear("SMS"))
                .thenThrow(new IllegalArgumentException("Tipo de notificacion no soportado: SMS"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> mensajeService.enviarMensaje(mensaje));

        // El mensaje nunca debe guardarse si falla la notificacion
        verify(mensajeRepository, never()).save(any());
    }
}