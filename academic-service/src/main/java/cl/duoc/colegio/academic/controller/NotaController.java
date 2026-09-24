package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.model.Evaluacion;
import cl.duoc.colegio.academic.model.Nota;
import cl.duoc.colegio.academic.repository.EstudianteRepository;
import cl.duoc.colegio.academic.repository.EvaluacionRepository;
import cl.duoc.colegio.academic.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
@RequiredArgsConstructor
public class NotaController {

    private final NotaRepository notaRepository;
    private final EstudianteRepository estudianteRepository;
    private final EvaluacionRepository evaluacionRepository;

    @GetMapping
    public List<Nota> listarTodas() {
        return notaRepository.findAll();
    }

    @PostMapping
    public Nota guardar(@RequestBody Nota nota) {
        Long estudianteId = nota.getEstudiante() != null ? nota.getEstudiante().getId() : null;
        Long evaluacionId = nota.getEvaluacion() != null ? nota.getEvaluacion().getId() : null;

        if (estudianteId == null || evaluacionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estudiante y evaluacion son obligatorios");
        }

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estudiante no encontrado"));

        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evaluacion no encontrada"));

        nota.setEstudiante(estudiante);
        nota.setEvaluacion(evaluacion);
        return notaRepository.save(nota);
    }

    @GetMapping("/estudiante/{id}")
    public List<Nota> listarPorEstudiante(@PathVariable Long id){
        return notaRepository.findByEstudianteId(id);
    }
}