package cl.duoc.colegio.communication.messaging.event;

public record NotaRegistradaEvent(
        Long notaId,
        Long estudianteId,
        String estudianteNombre,
        String evaluacionNombre,
        String asignaturaNombre,
        Double valor
) {}
