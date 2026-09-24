package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Apoderado;
import cl.duoc.colegio.academic.model.Docente;
import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.repository.ApoderadoRepository;
import cl.duoc.colegio.academic.repository.DocenteRepository;
import cl.duoc.colegio.academic.repository.EstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthAcademicControllerTest {
    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private ApoderadoRepository apoderadoRepository;

    @Mock
    private DocenteRepository docenteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthAcademicController authAcademicController;

    @BeforeEach
    void setUp() {
        authAcademicController = new AuthAcademicController(
                estudianteRepository,
                apoderadoRepository,
                docenteRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("validar debe retornar token para estudiante con credenciales correctas")
    void testValidarEstudianteValido() {
        // Arrange
        String rut = "33333333-3";
        String password = "123456";

        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setRut(rut);
        estudiante.setNombre("Martina");
        estudiante.setPassword("$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO");

        Map<String, String> credenciales = Map.of(
                "rut", rut,
                "password", password
        );

        when(estudianteRepository.findByRut(rut)).thenReturn(Optional.of(estudiante));
        when(passwordEncoder.matches(password, estudiante.getPassword())).thenReturn(true);

        // Act
        ResponseEntity<?> respuesta = authAcademicController.validar(credenciales);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(estudianteRepository, times(1)).findByRut(rut);
        verify(passwordEncoder, times(1)).matches(password, estudiante.getPassword());
    }

    @Test
    @DisplayName("validar debe retornar token para apoderado con credenciales correctas")
    void testValidarApoderadoValido() {
        // Arrange
        String rut = "55555555-5";
        String password = "123456";

        Estudiante estudiante1 = new Estudiante();
        estudiante1.setId(1L);
        estudiante1.setNombre("Martina");

        Apoderado apoderado = new Apoderado();
        apoderado.setId(1L);
        apoderado.setRut(rut);
        apoderado.setNombre("Carolina");
        apoderado.setPassword("$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO");
        apoderado.setEstudiantes(new ArrayList<>(List.of(estudiante1)));

        Map<String, String> credenciales = Map.of(
                "rut", rut,
                "password", password
        );

        when(apoderadoRepository.findByRut(rut)).thenReturn(Optional.of(apoderado));
        when(passwordEncoder.matches(password, apoderado.getPassword())).thenReturn(true);

        // Act
        ResponseEntity<?> respuesta = authAcademicController.validar(credenciales);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(apoderadoRepository, times(1)).findByRut(rut);
        verify(passwordEncoder, times(1)).matches(password, apoderado.getPassword());
    }

    @Test
    @DisplayName("validar debe retornar token para docente con credenciales correctas")
    void testValidarDocenteValido() {
        // Arrange
        String rut = "11111111-1";
        String password = "123456";

        Docente docente = new Docente();
        docente.setId(1L);
        docente.setRut(rut);
        docente.setNombre("Roberto");
        docente.setPassword("$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO");

        Map<String, String> credenciales = Map.of(
                "rut", rut,
                "password", password
        );

        when(estudianteRepository.findByRut(rut)).thenReturn(Optional.empty());
        when(apoderadoRepository.findByRut(rut)).thenReturn(Optional.empty());
        when(docenteRepository.findByRut(rut)).thenReturn(Optional.of(docente));
        when(passwordEncoder.matches(password, docente.getPassword())).thenReturn(true);

        // Act
        ResponseEntity<?> respuesta = authAcademicController.validar(credenciales);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        verify(estudianteRepository, times(1)).findByRut(rut);
        verify(apoderadoRepository, times(1)).findByRut(rut);
        verify(docenteRepository, times(1)).findByRut(rut);
        verify(passwordEncoder, times(1)).matches(password, docente.getPassword());
    }

    @Test
    @DisplayName("validar debe retornar error si las credenciales son incorrectas")
    void testValidarCredencialesIncorrectas() {
        // Arrange
        String rut = "33333333-3";
        String password = "incorrecta";

        Estudiante estudiante = new Estudiante();
        estudiante.setRut(rut);
        estudiante.setPassword("$2a$10$ss9kwE8iSIqcJOAPhZR0Y.2XdYXJTFJ1/wGq6SUv74vULE7uhKUIO");

        Map<String, String> credenciales = Map.of(
                "rut", rut,
                "password", password
        );

        when(estudianteRepository.findByRut(rut)).thenReturn(Optional.of(estudiante));
        when(passwordEncoder.matches(password, estudiante.getPassword())).thenReturn(false);

        // Act
        ResponseEntity<?> respuesta = authAcademicController.validar(credenciales);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().toString().contains("error"));
        verify(passwordEncoder, times(1)).matches(password, estudiante.getPassword());
    }

    @Test
    @DisplayName("validar debe retornar error si el usuario no existe")
    void testValidarUsuarioNoExistente() {
        // Arrange
        String rut = "99999999-9";
        String password = "123456";

        Map<String, String> credenciales = Map.of(
                "rut", rut,
                "password", password
        );

        when(estudianteRepository.findByRut(rut)).thenReturn(Optional.empty());
        when(apoderadoRepository.findByRut(rut)).thenReturn(Optional.empty());
        when(docenteRepository.findByRut(rut)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> respuesta = authAcademicController.validar(credenciales);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().toString().contains("error"));
        verify(estudianteRepository, times(1)).findByRut(rut);
        verify(apoderadoRepository, times(1)).findByRut(rut);
        verify(docenteRepository, times(1)).findByRut(rut);
    }
}

