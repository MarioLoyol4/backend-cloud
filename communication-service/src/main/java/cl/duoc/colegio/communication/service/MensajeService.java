package cl.duoc.colegio.communication.service;


import cl.duoc.colegio.communication.model.Mensaje;
import cl.duoc.colegio.communication.notificacion.Notificacion;
import cl.duoc.colegio.communication.notificacion.NotificacionFactory;
import cl.duoc.colegio.communication.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeService {

    @Autowired
    private final MensajeRepository mensajeRepository;

    @Autowired
    private final NotificacionFactory notificacionFactory;

    public Mensaje enviarMensaje(Mensaje mensaje) {
        Notificacion notificacion = notificacionFactory.crear(mensaje.getTipoNotificacion());
        notificacion.enviar(mensaje.getDestinatarioId(), mensaje.getContenido());

        return mensajeRepository.save(mensaje);
    }

    public List<Mensaje> getMensajesRecibidos(String destinatarioId) {
        return mensajeRepository.findByDestinatarioId(destinatarioId);
    }

    public List<Mensaje> getMensajesEnviados(String remitenteId) {
        return mensajeRepository.findByRemitenteId(remitenteId);
    }
}
