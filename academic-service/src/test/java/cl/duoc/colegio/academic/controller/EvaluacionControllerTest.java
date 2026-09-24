package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Asignatura;
import cl.duoc.colegio.academic.model.Evaluacion;
import cl.duoc.colegio.academic.repository.AsignaturaRepository;
import cl.duoc.colegio.academic.repository.EvaluacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluacionControllerTest {
    @Mock
    private EvaluacionRepository evaluacionRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    private EvaluacionController evaluacionController;

    @BeforeEach
    void setUp() {
        evaluacionController = new EvaluacionController(evaluacionRepository, asignaturaRepository);
    }

    @Test
    @DisplayName("listar debe retornar todas las evaluaciones")
    void testListar() {
        // Arrange
        Asignatura asignatura1 = new Asignatura(1L, "Matemáticas");
        Asignatura asignatura2 = new Asignatura(2L, "Lenguaje y Comunicación");

        Evaluacion evaluacion1 = new Evaluacion();
        evaluacion1.setId(1L);
        evaluacion1.setNombre("Prueba de Álgebra");
        evaluacion1.setFecha(LocalDate.of(2026, 5, 1));
        evaluacion1.setAsignatura(asignatura1);

        Evaluacion evaluacion2 = new Evaluacion();
        evaluacion2.setId(2L);
        evaluacion2.setNombre("Control de Geometría");
        evaluacion2.setFecha(LocalDate.of(2026, 5, 15));
        evaluacion2.setAsignatura(asignatura1);

        Evaluacion evaluacion3 = new Evaluacion();
        evaluacion3.setId(3L);
        evaluacion3.setNombre("Lectura Complementaria");
        evaluacion3.setFecha(LocalDate.of(2026, 5, 10));
        evaluacion3.setAsignatura(asignatura2);

        when(evaluacionRepository.findAll()).thenReturn(List.of(evaluacion1, evaluacion2, evaluacion3));

        // Act
        List<Evaluacion> resultado = evaluacionController.listar();

        // Assert
        assertEquals(3, resultado.size());
        assertEquals("Prueba de Álgebra", resultado.get(0).getNombre());
        assertEquals("Control de Geometría", resultado.get(1).getNombre());
        assertEquals("Lectura Complementaria", resultado.get(2).getNombre());
        verify(evaluacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listar debe retornar lista vacía si no hay evaluaciones")
    void testListarVacio() {
        // Arrange
        when(evaluacionRepository.findAll()).thenReturn(List.of());

        // Act
        List<Evaluacion> resultado = evaluacionController.listar();

        // Assert
        assertTrue(resultado.isEmpty());
        verify(evaluacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("crearEvaluacion debe guardar una evaluación correctamente")
    void testCrearEvaluacion() {
        // Arrange
        Asignatura asignatura = new Asignatura(1L, "Matemáticas");

        Evaluacion evaluacionNueva = new Evaluacion();
        evaluacionNueva.setNombre("Prueba de Álgebra");
        evaluacionNueva.setFecha(LocalDate.of(2026, 5, 1));
        evaluacionNueva.setAsignatura(asignatura);

        Evaluacion evaluacionGuardada = new Evaluacion();
        evaluacionGuardada.setId(1L);
        evaluacionGuardada.setNombre("Prueba de Álgebra");
        evaluacionGuardada.setFecha(LocalDate.of(2026, 5, 1));
        evaluacionGuardada.setAsignatura(asignatura);

        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignatura));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacionGuardada);

        // Act
        Evaluacion resultado = evaluacionController.crearEvaluacion(evaluacionNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Prueba de Álgebra", resultado.getNombre());
        assertEquals(LocalDate.of(2026, 5, 1), resultado.getFecha());
        assertEquals("Matemáticas", resultado.getAsignatura().getNombre());
        verify(evaluacionRepository, times(1)).save(any(Evaluacion.class));
    }

    @Test
    @DisplayName("crearEvaluacion debe guardar múltiples evaluaciones")
    void testCrearMultiplesEvaluaciones() {
        // Arrange
        Asignatura asignatura1 = new Asignatura(1L, "Matemáticas");
        Asignatura asignatura2 = new Asignatura(2L, "Lenguaje");

        Evaluacion evaluacion1 = new Evaluacion();
        evaluacion1.setNombre("Prueba 1");
        evaluacion1.setFecha(LocalDate.of(2026, 5, 1));
        evaluacion1.setAsignatura(asignatura1);

        Evaluacion evaluacion2 = new Evaluacion();
        evaluacion2.setNombre("Prueba 2");
        evaluacion2.setFecha(LocalDate.of(2026, 5, 10));
        evaluacion2.setAsignatura(asignatura2);

        Evaluacion evaluacionGuardada1 = new Evaluacion();
        evaluacionGuardada1.setId(1L);
        evaluacionGuardada1.setNombre("Prueba 1");
        evaluacionGuardada1.setFecha(LocalDate.of(2026, 5, 1));
        evaluacionGuardada1.setAsignatura(asignatura1);

        Evaluacion evaluacionGuardada2 = new Evaluacion();
        evaluacionGuardada2.setId(2L);
        evaluacionGuardada2.setNombre("Prueba 2");
        evaluacionGuardada2.setFecha(LocalDate.of(2026, 5, 10));
        evaluacionGuardada2.setAsignatura(asignatura2);

        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignatura1));
        when(asignaturaRepository.findById(2L)).thenReturn(Optional.of(asignatura2));
        when(evaluacionRepository.save(evaluacion1)).thenReturn(evaluacionGuardada1);
        when(evaluacionRepository.save(evaluacion2)).thenReturn(evaluacionGuardada2);

        // Act
        Evaluacion resultado1 = evaluacionController.crearEvaluacion(evaluacion1);
        Evaluacion resultado2 = evaluacionController.crearEvaluacion(evaluacion2);

        // Assert
        assertEquals("Prueba 1", resultado1.getNombre());
        assertEquals("Prueba 2", resultado2.getNombre());
        assertEquals(1L, resultado1.getId());
        assertEquals(2L, resultado2.getId());
        verify(evaluacionRepository, times(2)).save(any(Evaluacion.class));
    }
}

