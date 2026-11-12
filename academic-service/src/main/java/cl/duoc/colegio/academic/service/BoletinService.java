package cl.duoc.colegio.academic.service;

import cl.duoc.colegio.academic.messaging.event.AsignaturaPromedio;
import cl.duoc.colegio.academic.messaging.event.BoletinGeneradoEvent;
import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.model.Nota;
import cl.duoc.colegio.academic.repository.EstudianteRepository;
import cl.duoc.colegio.academic.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoletinService {
    private final EstudianteRepository estudianteRepository;
    private final NotaRepository notaRepository;

    @Transactional(readOnly = true)
    public BoletinGeneradoEvent generar(Long estudianteId) {
        Estudiante e = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante " + estudianteId + " no existe"));

        Map<String, Double> porAsignatura = notaRepository.findByEstudianteId(estudianteId).stream()
                .collect(Collectors.groupingBy(
                        n -> n.getEvaluacion().getAsignatura().getNombre(),
                        TreeMap::new,
                        Collectors.averagingDouble(Nota::getValor)
                ));

        List<AsignaturaPromedio> detalle = porAsignatura.entrySet().stream()
                .map(x -> new AsignaturaPromedio(x.getKey(), redondear(x.getValue())))
                .toList();

        double general = detalle.stream().mapToDouble(AsignaturaPromedio::promedio).average().orElse(0);

        return new BoletinGeneradoEvent(e.getId(), e.getNombre() + " " + e.getApellido(), redondear(general), detalle);
    }

    private double redondear(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
