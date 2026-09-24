package cl.duoc.colegio.communication.notificacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificacionFactoryTest {

    private final NotificacionFactory factory = new NotificacionFactory();

    @Test
    @DisplayName("Debe crear una notificacionEmail cuando el tipo es EMAIL")
    void crearNotificacionEmail() {
        Notificacion notificacion = factory.crear("EMAIL");

        assertNotNull(notificacion);
        assertInstanceOf(NotifiacionEmail.class, notificacion);
        assertEquals("EMAIL", notificacion.getTipo());
    }

    @Test
    @DisplayName("Debe crear una NotificacionPlataforma cuando el tipo es PLATAFORMA")
    void crearNotificacionPlataforma() {
        Notificacion notificacion = factory.crear("PLATAFORMA");

        assertNotNull(notificacion);
        assertInstanceOf(NotificacionPlataforma.class, notificacion);
        assertEquals("PLATAFORMA", notificacion.getTipo());
    }
    @Test
    @DisplayName("Debe ser insensible a mayusculas/minusculas")
    void testCrearEsCaseInsensitive() {
        Notificacion notificacion = factory.crear("email");

        assertInstanceOf(NotifiacionEmail.class, notificacion);
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el tipo no es soportado")
    void testCrearNotificacionNoSoportado() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.crear("SMS")
        );

        assertTrue(exception.getMessage().contains("no soportado"));
    }

    @Test
    @DisplayName("NotificacioNEmail debe ejecutar enviar sin lanzar excepciones")
    void testNotificacionEmailEnviar() {
        Notificacion notificacion = factory.crear("EMAIL");

        assertDoesNotThrow(() ->
                notificacion.enviar("apoderado@colegio.cl", "mensaje de prueba"));
    }

    @Test
    @DisplayName("NotificacionPlataforma debe ejecutar enviar sin lanzar excepciones")
    void testNotificacionPlataformaEnviar() {
        Notificacion notificacion = factory.crear("PLATAFORMA");

        assertDoesNotThrow(() ->
                notificacion.enviar("usuario-1", "mensaje interno de prueba"));
    }
}
