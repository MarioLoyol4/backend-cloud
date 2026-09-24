package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Estudiante;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteControllerTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EstudianteController estudianteController;

    @BeforeEach
    void setUp() {
        estudianteController = new EstudianteController(estudianteRepository, passwordEncoder);
    }

    @Test
    @DisplayName("listarTodos debe retornar todos los estudiantes")
    void testListarTodos() {
        when(estudianteRepository.findAll())
                .thenReturn(List.of(new Estudiante(), new Estudiante()));

        List<Estudiante> resultado = estudianteController.listarTodos();

        assertEquals(2, resultado.size());
        verify(estudianteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("guardar debe encriptar la contrasena antes de guardar")
    void testGuardar() {
        Estudiante estudiante = new Estudiante();
        estudiante.setRut("99999999-9");
        estudiante.setPassword("estudiante123");

        when(passwordEncoder.encode("estudiante123")).thenReturn("hash-encriptado");
        when(estudianteRepository.save(any(Estudiante.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Estudiante resultado = estudianteController.guardar(estudiante);

        assertEquals("hash-encriptado", resultado.getPassword());
        verify(passwordEncoder, times(1)).encode("estudiante123");
        verify(estudianteRepository, times(1)).save(estudiante);
    }

    @Test
    @DisplayName("listarPorCurso debe retornar los estudiantes de un curso")
    void testListarPorCurso() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);

        when(estudianteRepository.findByCursoId(1L)).thenReturn(List.of(estudiante));

        ResponseEntity<List<Estudiante>> respuesta = estudianteController.listarPorCurso(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
    }

    @Test
    @DisplayName("listarPorCurso debe retornar lista vacia si el curso no tiene estudiantes")
    void testListarPorCursoVacio() {
        when(estudianteRepository.findByCursoId(99L)).thenReturn(List.of());

        ResponseEntity<List<Estudiante>> respuesta = estudianteController.listarPorCurso(99L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().isEmpty());
    }
}