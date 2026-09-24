package cl.duoc.colegio.attendance.controller;

import cl.duoc.colegio.attendance.model.Asistencia;
import cl.duoc.colegio.attendance.repository.AsistenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {


    private final AsistenciaRepository asistenciaRepository;

    @PostMapping
    public Asistencia registrar(@RequestBody Asistencia asistencia){
        return asistenciaRepository.save(asistencia);
    }

    @GetMapping("/estudiante/{id}")
    public List<Asistencia> listarPorEstudiante(@PathVariable Long id) {
        return asistenciaRepository.findByEstudianteId(id);
    }
}
