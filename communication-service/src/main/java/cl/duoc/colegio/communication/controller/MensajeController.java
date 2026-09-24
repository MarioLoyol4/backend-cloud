package cl.duoc.colegio.communication.controller;


import cl.duoc.colegio.communication.model.Mensaje;
import cl.duoc.colegio.communication.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeController {


    private final MensajeService mensajeService;

    @PostMapping
    public ResponseEntity<Mensaje> enviar(@RequestBody Mensaje mensaje) {
        return ResponseEntity.ok(mensajeService.enviarMensaje(mensaje));
    }

    @GetMapping("/recibidos/{destinatarioId}")
    public ResponseEntity<List<Mensaje>> recibidos(@PathVariable String destinatarioId){
        return ResponseEntity.ok(mensajeService.getMensajesRecibidos(destinatarioId));
    }

    @GetMapping("/enviados/{remitenteId}")
    public ResponseEntity<List<Mensaje>> enviados(@PathVariable String remitenteId) {
        return ResponseEntity.ok(mensajeService.getMensajesEnviados(remitenteId));
    }
}
