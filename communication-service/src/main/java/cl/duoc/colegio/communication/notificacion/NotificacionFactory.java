package cl.duoc.colegio.communication.notificacion;

import org.springframework.stereotype.Component;

@Component
public class NotificacionFactory {

    public Notificacion crear(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "EMAIL" -> new NotifiacionEmail();
            case "PLATAFORMA" -> new NotificacionPlataforma();
            default -> throw new IllegalArgumentException("Tipo de notificacion no soportado: " + tipo);
        };
    }
}
