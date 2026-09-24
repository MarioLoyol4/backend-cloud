package cl.duoc.colegio.communication.controller;

import cl.duoc.colegio.communication.model.Comunicado;
import cl.duoc.colegio.communication.service.ComunicadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunicados")
@RequiredArgsConstructor
public class ComunicadoController {


    private final ComunicadoService comunicadoService;

    @PostMapping
    public ResponseEntity<Comunicado> publicar(@RequestBody Comunicado comunicado) {
        return ResponseEntity.ok(comunicadoService.publicar(comunicado));
    }

    @GetMapping
    public ResponseEntity<List<Comunicado>> todos() {
        return ResponseEntity.ok(comunicadoService.getTodos());
    }

    @GetMapping("/destinatario/{destinatario}")
    public ResponseEntity<List<Comunicado>> porDestinatario(@PathVariable String destinatario) {
        return ResponseEntity.ok(comunicadoService.getByDestinatario(destinatario));
    }
}
