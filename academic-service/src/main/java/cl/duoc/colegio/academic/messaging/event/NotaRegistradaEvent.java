package cl.duoc.colegio.academic.messaging.event;

public record NotaRegistradaEvent(
        Long notaId,
        Long estudianteId,
        String estudianteNombre,
        String evaluacion,
        String asignatura,
        Double valor
){}
