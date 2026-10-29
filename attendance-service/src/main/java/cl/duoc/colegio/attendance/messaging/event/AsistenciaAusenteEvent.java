package cl.duoc.colegio.attendance.messaging.event;

public record AsistenciaAusenteEvent(Long estudianteId, String fecha) {}