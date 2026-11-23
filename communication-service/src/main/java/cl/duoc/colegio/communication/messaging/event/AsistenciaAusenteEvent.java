package cl.duoc.colegio.communication.messaging.event;

public record AsistenciaAusenteEvent(Long estudianteId, String fecha) {}