package cl.duoc.colegio.communication.repository;

import cl.duoc.colegio.communication.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByDestinatarioId(String destinatarioId);
    List<Mensaje> findByRemitenteId(String remitenteId);
}
