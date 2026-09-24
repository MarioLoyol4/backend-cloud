package cl.duoc.colegio.communication.notificacion;

public class NotificacionPlataforma implements Notificacion{

    @Override
    public void enviar(String destinatario, String contenido){
        System.out.println("[PLATAFORMA] Para: " + destinatario + " | Mensaje: " + contenido);
    }

    @Override
    public String getTipo(){
        return "PLATAFORMA";
    }
}
