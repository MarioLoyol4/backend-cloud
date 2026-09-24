package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Asignatura;
import cl.duoc.colegio.academic.repository.AsignaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
@RequiredArgsConstructor
public class AsignaturaController {

    private final AsignaturaRepository asignaturaRepository;

    @GetMapping
    public List<Asignatura> listarTodas() {
        return asignaturaRepository.findAll();
    }

    @PostMapping
    public Asignatura guardar(@RequestBody Asignatura asignatura) {
        return asignaturaRepository.save(asignatura);
    }
}
