package cl.duoc.colegio.academic.repository;

import cl.duoc.colegio.academic.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByRut(String rut);
    List<Estudiante> findByCursoId(Long cursoId);
}
