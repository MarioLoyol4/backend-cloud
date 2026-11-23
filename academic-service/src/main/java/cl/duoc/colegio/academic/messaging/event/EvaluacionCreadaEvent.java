package cl.duoc.colegio.academic.messaging.event;

public record EvaluacionCreadaEvent(Long evaluacionId, String nombre, String asignatura) {}