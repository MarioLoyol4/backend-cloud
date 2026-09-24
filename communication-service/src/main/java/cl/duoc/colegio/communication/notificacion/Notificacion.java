package cl.duoc.colegio.communication.notificacion;

public interface Notificacion {
    void enviar(String destinatario, String contenido);
    String getTipo();
}
