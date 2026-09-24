package cl.duoc.colegio.academic.controller;


import cl.duoc.colegio.academic.model.Apoderado;
import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.repository.ApoderadoRepository;
import cl.duoc.colegio.academic.repository.EstudianteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apoderados")
@RequiredArgsConstructor
public class ApoderadoController {


    private final ApoderadoRepository apoderadoRepository;


    private final PasswordEncoder passwordEncoder;


    private final EstudianteRepository estudianteRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Apoderado> crear(@RequestBody Apoderado apoderado) {
        apoderado.setPassword(passwordEncoder.encode(apoderado.getPassword()));
        return ResponseEntity.ok(apoderadoRepository.save(apoderado));
    }

    @GetMapping
    public ResponseEntity<List<Apoderado>> obtenerTodos() {
        return ResponseEntity.ok(apoderadoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Apoderado> obtenerPorId(@RequestBody Long id) {
        return apoderadoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/estudiantes")
    public ResponseEntity<List<Estudiante>> obtenerEstudiantes(@PathVariable Long id) {
        return apoderadoRepository.findById(id)
                .map(a -> ResponseEntity.ok(a.getEstudiantes()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{apoderadoId}/estudiantes/{estudianteId}")
    @Transactional
    public ResponseEntity<Apoderado> asignarEstudiante(
            @PathVariable Long apoderadoId,
            @PathVariable Long estudianteId) {

        Apoderado apoderado = apoderadoRepository.findById(apoderadoId)
                .orElseThrow(() -> new RuntimeException("Apoderado no encontrado"));

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        if (!apoderado.getEstudiantes().contains(estudiante)) {
            apoderado.getEstudiantes().add(estudiante);
        }

        return ResponseEntity.ok(apoderadoRepository.save(apoderado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        apoderadoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
