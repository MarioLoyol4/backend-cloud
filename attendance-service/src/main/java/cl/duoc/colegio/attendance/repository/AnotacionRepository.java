package cl.duoc.colegio.attendance.repository;

import cl.duoc.colegio.attendance.model.Anotacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnotacionRepository extends JpaRepository<Anotacion, Long> {
    List<Anotacion> findByEstudianteId(Long estudianteId);
}
