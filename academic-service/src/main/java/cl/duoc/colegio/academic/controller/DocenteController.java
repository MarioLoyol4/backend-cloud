package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Asignatura;
import cl.duoc.colegio.academic.model.Docente;
import cl.duoc.colegio.academic.repository.AsignaturaRepository;
import cl.duoc.colegio.academic.repository.DocenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docentes")
@RequiredArgsConstructor
public class DocenteController {


    private final DocenteRepository docenteRepository;


    private final AsignaturaRepository asignaturaRepository;


    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Docente> crear(@RequestBody Docente docente) {
        docente.setPassword(passwordEncoder.encode(docente.getPassword()));
        return ResponseEntity.ok(docenteRepository.save(docente));
    }

    @GetMapping
    public ResponseEntity<List<Docente>> obtenerTodos() {
        return ResponseEntity.ok(docenteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Docente> obtenerPorId(@PathVariable Long id) {
        return docenteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{docenteId}/asignaturas/{asignaturaId}")
    @Transactional
    public ResponseEntity<Docente> asignarAsignatura(
            @PathVariable Long docenteId,
            @PathVariable Long asignaturaId    ) {
        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        Asignatura asignatura = asignaturaRepository.findById(asignaturaId)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));

        docente.getAsignaturas().add(asignatura);
        return ResponseEntity.ok(docenteRepository.save(docente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        docenteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
