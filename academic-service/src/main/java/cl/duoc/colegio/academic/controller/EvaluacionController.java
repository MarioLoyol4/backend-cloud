package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Evaluacion;
import cl.duoc.colegio.academic.repository.EvaluacionRepository;
import cl.duoc.colegio.academic.service.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionRepository evaluacionRepository;
    private final EvaluacionService evaluacionService;


    @PostMapping
    public Evaluacion crearEvaluacion(@RequestBody Evaluacion evaluacion) {
        return evaluacionService.crear(evaluacion);
    }
}