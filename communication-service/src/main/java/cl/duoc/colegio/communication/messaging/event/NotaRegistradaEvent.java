package cl.duoc.colegio.communication.messaging.event;

public record NotaRegistradaEvent(Long notaId, Long estudianteId, String estudianteNombre,
                                  String evaluacion, String asignatura, Double valor) {}