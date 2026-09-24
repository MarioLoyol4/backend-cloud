package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Apoderado;
import cl.duoc.colegio.academic.model.Docente;
import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.repository.ApoderadoRepository;
import cl.duoc.colegio.academic.repository.DocenteRepository;
import cl.duoc.colegio.academic.repository.EstudianteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth-academic")
@RequiredArgsConstructor
public class AuthAcademicController {

    private static final String ERROR_KEY = "error";
    private static final String ERROR_MSG_CREDENTIALS = "Credenciales incorrectas";

    private final EstudianteRepository estudianteRepository;
    private final ApoderadoRepository apoderadoRepository;
    private final DocenteRepository docenteRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/validar")
    @Transactional
    public ResponseEntity<?> validar(@RequestBody Map<String, String> credenciales) {
        String rut = credenciales.get("rut");
        String password = credenciales.get("password");

        var apoderado = apoderadoRepository.findByRut(rut);

        if (apoderado.isPresent()) {
            Apoderado a = apoderado.get();

            if (!passwordEncoder.matches(password, a.getPassword())) {
                return ResponseEntity.status(401).body(Map.of(ERROR_KEY, ERROR_MSG_CREDENTIALS));
            }

            List<Long> estudiantesIds = a.getEstudiantes()
                    .stream()
                    .map(Estudiante::getId)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "referenciaId", String.valueOf(a.getId()),
                    "rol", "APODERADO",
                    "estudiantesACargo", estudiantesIds
            ));
        }

        var estudiante = estudianteRepository.findByRut(rut);
        if (estudiante.isPresent()) {
            Estudiante e = estudiante.get();

            if (!passwordEncoder.matches(password, e.getPassword())) {
                return ResponseEntity.status(401).body(Map.of(ERROR_KEY, ERROR_MSG_CREDENTIALS));
            }

            return ResponseEntity.ok(Map.of(
                    "referenciaId", String.valueOf(e.getId()),
                    "rol", "ESTUDIANTE",
                    "estudiantesACargo", List.of()
            ));
        }

        var docente = docenteRepository.findByRut(rut);
        if (docente.isPresent()) {
            Docente d = docente.get();
            if (!passwordEncoder.matches(password, d.getPassword())) {
                return ResponseEntity.status(401).body(Map.of(ERROR_KEY, ERROR_MSG_CREDENTIALS));
            }
            return ResponseEntity.ok(Map.of(
                    "referenciaId", String.valueOf(d.getId()),
                    "rol", "DOCENTE",
                    "estudiantesACargo", List.of()
            ));
        }


        return ResponseEntity.status(401).body(Map.of(ERROR_KEY, ERROR_MSG_CREDENTIALS));
    }
}
