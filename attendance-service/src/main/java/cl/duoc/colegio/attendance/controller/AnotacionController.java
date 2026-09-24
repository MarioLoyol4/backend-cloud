package cl.duoc.colegio.attendance.controller;

import cl.duoc.colegio.attendance.model.Anotacion;
import cl.duoc.colegio.attendance.repository.AnotacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anotaciones")
@RequiredArgsConstructor
public class AnotacionController {


    private final AnotacionRepository anotacionRepository;

    @PostMapping
    public Anotacion registrar(@RequestBody Anotacion anotacion) {
        return anotacionRepository.save(anotacion);
    }

    @GetMapping("/estudiante/{id}")
    public List<Anotacion> listarPorEstudiante(@PathVariable Long id){
        return anotacionRepository.findByEstudianteId(id);
    }
}
