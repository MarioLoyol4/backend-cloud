package cl.duoc.colegio.communication.messaging.event;

public record EvaluacionCreadaEvent(Long evaluacionId, String nombre, String asignatura) {}