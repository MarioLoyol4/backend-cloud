package cl.duoc.colegio.attendance.controller;

import cl.duoc.colegio.attendance.messaging.producer.AsistenciaEventPublisher;
import cl.duoc.colegio.attendance.model.Asistencia;
import cl.duoc.colegio.attendance.repository.AsistenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaRepository asistenciaRepository;
    private final AsistenciaEventPublisher eventPublisher;
    @PostMapping
    public Asistencia registrar(@RequestBody Asistencia asistencia){
        Asistencia guardada = asistenciaRepository.save(asistencia);
        eventPublisher.asistenciaRegistrada(guardada);
        return guardada;
    }

    @GetMapping("/estudiante/{id}")
    public List<Asistencia> listarPorEstudiante(@PathVariable Long id) {
        return asistenciaRepository.findByEstudianteId(id);
    }
}