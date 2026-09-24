package cl.duoc.colegio.academic.repository;

import cl.duoc.colegio.academic.model.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApoderadoRepository extends JpaRepository<Apoderado, Long> {
    Optional<Apoderado> findByRut(String rut);
    Optional<Apoderado> findByEmail(String email);
}
