package cl.duoc.colegio.communication.notificacion;

public class NotifiacionEmail implements Notificacion{

    @Override
    public void enviar(String destinatario, String contenido) {
        System.out.println("[EMAIL] Para: " + destinatario + " | Mensaje: " + contenido);
    }

    @Override
    public String getTipo(){
        return "EMAIL";
    }
}
