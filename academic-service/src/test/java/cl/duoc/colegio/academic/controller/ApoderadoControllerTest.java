package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Apoderado;
import cl.duoc.colegio.academic.model.Estudiante;
import cl.duoc.colegio.academic.repository.ApoderadoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApoderadoControllerTest {

    @Mock
    private ApoderadoRepository apoderadoRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ApoderadoController apoderadoController;

    @BeforeEach
    void setUp() {
        apoderadoController = new ApoderadoController(
                apoderadoRepository,  passwordEncoder, estudianteRepository
        );
    }

    @Test
    @DisplayName("crear debe encriptar la contrasena antes de guardar")
    void testCrear() {
        Apoderado apoderado = new Apoderado();
        apoderado.setRut("12345678-9");
        apoderado.setPassword("apoderado123");
        apoderado.setEstudiantes(new ArrayList<>());

        when(passwordEncoder.encode("apoderado123")).thenReturn("hash-encriptado");
        when(apoderadoRepository.save(any(Apoderado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Apoderado> respuesta = apoderadoController.crear(apoderado);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("hash-encriptado", respuesta.getBody().getPassword());
        verify(passwordEncoder, times(1)).encode("apoderado123");
        verify(apoderadoRepository, times(1)).save(apoderado);
    }

    @Test
    @DisplayName("obtenerTodos debe retornar la lista completa de apoderados")
    void testObtenerTodos() {
        when(apoderadoRepository.findAll())
                .thenReturn(List.of(new Apoderado(), new Apoderado()));

        ResponseEntity<List<Apoderado>> respuesta = apoderadoController.obtenerTodos();

        assertEquals(2, respuesta.getBody().size());
    }

    @Test
    @DisplayName("obtenerPorId debe retornar 200 si el apoderado existe")
    void testObtenerPorIdExiste() {
        Apoderado apoderado = new Apoderado();
        apoderado.setId(1L);
        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderado));

        ResponseEntity<Apoderado> respuesta = apoderadoController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().getId());
    }

    @Test
    @DisplayName("obtenerPorId debe retornar 404 si el apoderado no existe")
    void testObtenerPorIdNoExiste() {
        when(apoderadoRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Apoderado> respuesta = apoderadoController.obtenerPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    @Test
    @DisplayName("obtenerEstudiantes debe retornar los estudiantes a cargo del apoderado")
    void testObtenerEstudiantes() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);

        Apoderado apoderado = new Apoderado();
        apoderado.setId(1L);
        apoderado.setEstudiantes(List.of(estudiante));

        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderado));

        ResponseEntity<List<Estudiante>> respuesta = apoderadoController.obtenerEstudiantes(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
    }

    @Test
    @DisplayName("asignarEstudiante debe agregar el estudiante a la lista del apoderado")
    void testAsignarEstudiante() {
        Apoderado apoderado = new Apoderado();
        apoderado.setId(1L);
        apoderado.setEstudiantes(new ArrayList<>());

        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);

        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderado));
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(apoderadoRepository.save(any(Apoderado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Apoderado> respuesta = apoderadoController.asignarEstudiante(1L, 1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().getEstudiantes().size());
        assertTrue(respuesta.getBody().getEstudiantes().contains(estudiante));
    }

    @Test
    @DisplayName("asignarEstudiante no debe duplicar si el estudiante ya esta asignado")
    void testAsignarEstudianteYaAsignado() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);

        Apoderado apoderado = new Apoderado();
        apoderado.setId(1L);
        apoderado.setEstudiantes(new ArrayList<>(List.of(estudiante)));

        when(apoderadoRepository.findById(1L)).thenReturn(Optional.of(apoderado));
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(apoderadoRepository.save(any(Apoderado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Apoderado> respuesta = apoderadoController.asignarEstudiante(1L, 1L);

        assertEquals(1, respuesta.getBody().getEstudiantes().size());
    }

    @Test
    @DisplayName("asignarEstudiante debe lanzar excepcion si el apoderado no existe")
    void testAsignarEstudianteApoderadoNoExiste() {
        when(apoderadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> apoderadoController.asignarEstudiante(99L, 1L));
    }

    @Test
    @DisplayName("eliminar debe llamar a deleteById y retornar 204")
    void testEliminar() {
        ResponseEntity<Void> respuesta = apoderadoController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(apoderadoRepository, times(1)).deleteById(1L);
    }
}