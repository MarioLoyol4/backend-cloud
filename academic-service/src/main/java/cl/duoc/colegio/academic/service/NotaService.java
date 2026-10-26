package cl.duoc.colegio.academic.service;

import cl.duoc.colegio.academic.config.MessagingProperties;
import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.model.Evaluacion;
import cl.duoc.colegio.academic.model.Nota;
import cl.duoc.colegio.academic.repository.EstudianteRepository;
import cl.duoc.colegio.academic.repository.EvaluacionRepository;
import cl.duoc.colegio.academic.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final EstudianteRepository estudianteRepository;
    private final EvaluacionRepository evaluacionRepository;

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties props;

    public Nota guardarNota(Nota nota) {
        Long estudianteId = nota.getEstudiante() != null ? nota.getEstudiante().getId() : null;
        Long evaluacionId = nota.getEvaluacion() != null ? nota.getEvaluacion().getId() : null;

        if (estudianteId == null || evaluacionId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estudiante y evaluacion son obligatorios");
        }

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estudiante no encontrado"));

        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evaluacion no encontrada"));

        nota.setEstudiante(estudiante);
        nota.setEvaluacion(evaluacion);

        // 1. Guardamos en la base de datos (Lógica de negocio)
        Nota notaGuardada = notaRepository.save(nota);

        // 2. Enviamos el mensaje a RabbitMQ (Lógica de mensajería)
        try {
            String mensaje = "Nueva nota registrada: " + notaGuardada.getCalificacion() + " para el estudiante ID: " + estudianteId;

            rabbitTemplate.convertAndSend(
                    props.getExchanges().getEventos(),
                    props.getQueues().getNota().getRoutingKey(),
                    mensaje
            );
            System.out.println("Mensaje enviado a RabbitMQ exitosamente.");
        } catch (Exception e) {
            // Si RabbitMQ falla, la nota ya está guardada, pero dejamos un log del error
            System.err.println("Error al enviar mensaje a RabbitMQ: " + e.getMessage());
        }

        return notaGuardada;
    }
}

