package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Asignatura;
import cl.duoc.colegio.academic.repository.AsignaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignaturaControllerTest {
    
    @Mock
    private AsignaturaRepository asignaturaRepository;

    private AsignaturaController asignaturaController;

    @BeforeEach
    void setUp() {
        asignaturaController = new AsignaturaController(asignaturaRepository);
    }

    @Test
    @DisplayName("listarTodas debe retornar todas las asignaturas")
    void testListarTodas() {
        // Arrange
        Asignatura asignatura1 = new Asignatura(1L, "Matemáticas");
        Asignatura asignatura2 = new Asignatura(2L, "Lenguaje y Comunicación");
        Asignatura asignatura3 = new Asignatura(3L, "Historia y Geografía");

        when(asignaturaRepository.findAll()).thenReturn(List.of(asignatura1, asignatura2, asignatura3));

        // Act
        List<Asignatura> resultado = asignaturaController.listarTodas();

        // Assert
        assertEquals(3, resultado.size());
        assertEquals("Matemáticas", resultado.get(0).getNombre());
        assertEquals("Lenguaje y Comunicación", resultado.get(1).getNombre());
        assertEquals("Historia y Geografía", resultado.get(2).getNombre());
        verify(asignaturaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarTodas debe retornar lista vacía si no hay asignaturas")
    void testListarTodasVacio() {
        // Arrange
        when(asignaturaRepository.findAll()).thenReturn(List.of());

        // Act
        List<Asignatura> resultado = asignaturaController.listarTodas();

        // Assert
        assertTrue(resultado.isEmpty());
        verify(asignaturaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("guardar debe guardar una asignatura correctamente")
    void testGuardar() {
        // Arrange
        Asignatura asignaturaNueva = new Asignatura(null, "Educación Física");
        Asignatura asignaturaGuardada = new Asignatura(4L, "Educación Física");

        when(asignaturaRepository.save(any(Asignatura.class))).thenReturn(asignaturaGuardada);

        // Act
        Asignatura resultado = asignaturaController.guardar(asignaturaNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals(4L, resultado.getId());
        assertEquals("Educación Física", resultado.getNombre());
        verify(asignaturaRepository, times(1)).save(asignaturaNueva);
    }

    @Test
    @DisplayName("guardar debe guardar múltiples asignaturas")
    void testGuardarMultiples() {
        // Arrange
        Asignatura asignatura1 = new Asignatura(null, "Ciencias Naturales");
        Asignatura asignatura2 = new Asignatura(null, "Artes");

        Asignatura asignaturaGuardada1 = new Asignatura(5L, "Ciencias Naturales");
        Asignatura asignaturaGuardada2 = new Asignatura(6L, "Artes");

        when(asignaturaRepository.save(asignatura1)).thenReturn(asignaturaGuardada1);
        when(asignaturaRepository.save(asignatura2)).thenReturn(asignaturaGuardada2);

        // Act
        Asignatura resultado1 = asignaturaController.guardar(asignatura1);
        Asignatura resultado2 = asignaturaController.guardar(asignatura2);

        // Assert
        assertEquals("Ciencias Naturales", resultado1.getNombre());
        assertEquals("Artes", resultado2.getNombre());
        assertEquals(5L, resultado1.getId());
        assertEquals(6L, resultado2.getId());
        verify(asignaturaRepository, times(2)).save(any(Asignatura.class));
    }
}

