package cl.duoc.colegio.attendance.controller;

import cl.duoc.colegio.attendance.model.Asistencia;
import cl.duoc.colegio.attendance.repository.AsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaControllerTest {
    @Mock
    private AsistenciaRepository asistenciaRepository;

    private AsistenciaController asistenciaController;

    @BeforeEach
    void setUp() {
        asistenciaController = new AsistenciaController(asistenciaRepository);
    }

    @Test
    @DisplayName("registrar debe guardar una asistencia")
    void testRegistrar() {
        // Arrange
        Asistencia asistenciaNueva = new Asistencia();
        asistenciaNueva.setEstudianteId(1L);
        asistenciaNueva.setFecha(LocalDate.of(2026, 5, 10));
        asistenciaNueva.setEstado("PRESENTE");

        Asistencia asistenciaGuardada = new Asistencia();
        asistenciaGuardada.setId(1L);
        asistenciaGuardada.setEstudianteId(1L);
        asistenciaGuardada.setFecha(LocalDate.of(2026, 5, 10));
        asistenciaGuardada.setEstado("PRESENTE");

        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(asistenciaGuardada);

        // Act
        Asistencia resultado = asistenciaController.registrar(asistenciaNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PRESENTE", resultado.getEstado());
        verify(asistenciaRepository, times(1)).save(asistenciaNueva);
    }

    @Test
    @DisplayName("listarPorEstudiante debe retornar asistencias del estudiante")
    void testListarPorEstudiante() {
        // Arrange
        Long estudianteId = 1L;
        Asistencia asistencia1 = new Asistencia();
        asistencia1.setId(1L);
        asistencia1.setEstudianteId(estudianteId);
        asistencia1.setFecha(LocalDate.of(2026, 5, 10));
        asistencia1.setEstado("PRESENTE");

        Asistencia asistencia2 = new Asistencia();
        asistencia2.setId(2L);
        asistencia2.setEstudianteId(estudianteId);
        asistencia2.setFecha(LocalDate.of(2026, 5, 11));
        asistencia2.setEstado("PRESENTE");

        when(asistenciaRepository.findByEstudianteId(estudianteId))
                .thenReturn(List.of(asistencia1, asistencia2));

        // Act
        List<Asistencia> resultado = asistenciaController.listarPorEstudiante(estudianteId);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("PRESENTE", resultado.get(0).getEstado());
        assertEquals("PRESENTE", resultado.get(1).getEstado());
        verify(asistenciaRepository, times(1)).findByEstudianteId(estudianteId);
    }

    @Test
    @DisplayName("listarPorEstudiante debe retornar lista vacía si no hay asistencias")
    void testListarPorEstudianteVacio() {
        // Arrange
        Long estudianteId = 99L;
        when(asistenciaRepository.findByEstudianteId(estudianteId)).thenReturn(List.of());

        // Act
        List<Asistencia> resultado = asistenciaController.listarPorEstudiante(estudianteId);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(asistenciaRepository, times(1)).findByEstudianteId(estudianteId);
    }

    @Test
    @DisplayName("registrar debe guardar asistencia AUSENTE")
    void testRegistrarAusente() {
        // Arrange
        Asistencia asistenciaNueva = new Asistencia();
        asistenciaNueva.setEstudianteId(2L);
        asistenciaNueva.setFecha(LocalDate.of(2026, 5, 10));
        asistenciaNueva.setEstado("AUSENTE");

        Asistencia asistenciaGuardada = new Asistencia();
        asistenciaGuardada.setId(3L);
        asistenciaGuardada.setEstudianteId(2L);
        asistenciaGuardada.setFecha(LocalDate.of(2026, 5, 10));
        asistenciaGuardada.setEstado("AUSENTE");

        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(asistenciaGuardada);

        // Act
        Asistencia resultado = asistenciaController.registrar(asistenciaNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals("AUSENTE", resultado.getEstado());
        verify(asistenciaRepository, times(1)).save(asistenciaNueva);
    }
}

