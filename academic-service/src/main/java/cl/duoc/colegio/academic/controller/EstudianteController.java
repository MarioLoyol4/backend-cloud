package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteRepository estudianteRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Estudiante> listarTodos(){
        return estudianteRepository.findAll();
    }

    @PostMapping
    public Estudiante guardar(@RequestBody Estudiante estudiante) {
        estudiante.setPassword(passwordEncoder.encode(estudiante.getPassword()));
        return estudianteRepository.save(estudiante);
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Estudiante>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(estudianteRepository.findByCursoId(cursoId));
    }
}