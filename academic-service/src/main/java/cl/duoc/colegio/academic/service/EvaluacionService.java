package cl.duoc.colegio.academic.service;

import cl.duoc.colegio.academic.messaging.producer.EvaluacionEventPublisher;
import cl.duoc.colegio.academic.model.Asignatura;
import cl.duoc.colegio.academic.model.Evaluacion;
import cl.duoc.colegio.academic.repository.AsignaturaRepository;
import cl.duoc.colegio.academic.repository.EvaluacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final EvaluacionEventPublisher publisher;

    @Transactional
    public Evaluacion crear(Evaluacion evaluacion) {
        Long asignaturaId = evaluacion.getAsignatura() != null ? evaluacion.getAsignatura().getId() : null;
        if (asignaturaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La asignatura es obligatoria");
        }
        Asignatura asignatura = asignaturaRepository.findById(asignaturaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asignatura no encontrada"));
        evaluacion.setAsignatura(asignatura);
        Evaluacion guardada = evaluacionRepository.save(evaluacion);
        publisher.evaluacionCreada(guardada);
        return guardada;
    }
}