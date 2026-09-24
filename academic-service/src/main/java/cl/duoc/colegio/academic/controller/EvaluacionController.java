package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Asignatura;
import cl.duoc.colegio.academic.model.Evaluacion;
import cl.duoc.colegio.academic.repository.AsignaturaRepository;
import cl.duoc.colegio.academic.repository.EvaluacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {


    private final EvaluacionRepository evaluacionRepository;
    private final AsignaturaRepository asignaturaRepository;

    @GetMapping
    public List<Evaluacion> listar() {
        return evaluacionRepository.findAll();
    }

    @PostMapping
    public Evaluacion crearEvaluacion(@RequestBody Evaluacion evaluacion) {
        Long asignaturaId = evaluacion.getAsignatura() != null ? evaluacion.getAsignatura().getId() : null;
        if (asignaturaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La asignatura es obligatoria");
        }

        Asignatura asignatura = asignaturaRepository.findById(asignaturaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asignatura no encontrada"));

        evaluacion.setAsignatura(asignatura);
        return evaluacionRepository.save(evaluacion);
    }
}
