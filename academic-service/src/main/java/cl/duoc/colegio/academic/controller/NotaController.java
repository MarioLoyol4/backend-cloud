package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Nota;
import cl.duoc.colegio.academic.repository.NotaRepository;
import cl.duoc.colegio.academic.service.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
@RequiredArgsConstructor
public class NotaController {

    // Inyectamos el servicio y el repositorio de notas (solo para los GET)
    private final NotaService notaService;
    private final NotaRepository notaRepository;

    @GetMapping
    public List<Nota> listarTodas() {
        return notaRepository.findAll();
    }

    @PostMapping
    public Nota guardar(@RequestBody Nota nota) {
        // Delegamos toda la responsabilidad al servicio
        return notaService.guardarNota(nota);
    }

    @GetMapping("/estudiante/{id}")
    public List<Nota> listarPorEstudiante(@PathVariable Long id) {
        return notaRepository.findByEstudianteId(id);
    }
}
