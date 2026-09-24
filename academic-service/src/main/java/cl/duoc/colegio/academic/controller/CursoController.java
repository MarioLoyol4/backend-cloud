package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Curso;
import cl.duoc.colegio.academic.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {


    private final CursoRepository cursoRepository;

    @GetMapping
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    @PostMapping
    public Curso guardar (@RequestBody Curso curso){
        return cursoRepository.save(curso);
    }

    @PutMapping("/{id}")
    public Curso editar(@PathVariable Long id, @RequestBody Curso cursoActualizado) {
        return cursoRepository.findById(id)
                .map(curso -> {
                    curso.setNivel(cursoActualizado.getNivel());
                    curso.setLetra(cursoActualizado.getLetra());
                    curso.setAño(cursoActualizado.getAño());
                    return cursoRepository.save(curso);
                })
                .orElseGet(() -> {
                    cursoActualizado.setId(id);
                    return cursoRepository.save(cursoActualizado);
                });
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        cursoRepository.deleteById(id);
    }
}
