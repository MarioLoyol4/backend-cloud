package cl.duoc.colegio.communication.messaging.event;

import java.util.List;

public record BoletinGeneradoEvent(
        Long estudianteId,
        String estudianteNombre,
        double promedioGeneral,
        List<AsignaturaPromedio> detalle
) {}
