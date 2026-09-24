package cl.duoc.colegio.attendance.controller;
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
import cl.duoc.colegio.attendance.model.Anotacion;
import cl.duoc.colegio.attendance.repository.AnotacionRepository;

@ExtendWith(MockitoExtension.class)
public class AnotacionControllerTest {



    
    @Mock
    private AnotacionRepository anotacionRepository;

    private AnotacionController anotacionController;

    @BeforeEach
    void setUp() {
        anotacionController = new AnotacionController(anotacionRepository);
    }

    @Test
    @DisplayName("registrar debe guardar una anotación positiva")
    void testRegistrarPositiva() {
        // Arrange
        Anotacion anotacionNueva = new Anotacion();
        anotacionNueva.setEstudianteId(1L);
        anotacionNueva.setTipo("POSITIVA");
        anotacionNueva.setDescripcion("Excelente participación en la feria científica.");
        anotacionNueva.setFecha(LocalDate.of(2026, 5, 10));

        Anotacion anotacionGuardada = new Anotacion();
        anotacionGuardada.setId(1L);
        anotacionGuardada.setEstudianteId(1L);
        anotacionGuardada.setTipo("POSITIVA");
        anotacionGuardada.setDescripcion("Excelente participación en la feria científica.");
        anotacionGuardada.setFecha(LocalDate.of(2026, 5, 10));

        when(anotacionRepository.save(any(Anotacion.class))).thenReturn(anotacionGuardada);

        // Act
        Anotacion resultado = anotacionController.registrar(anotacionNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("POSITIVA", resultado.getTipo());
        verify(anotacionRepository, times(1)).save(anotacionNueva);
    }

    @Test
    @DisplayName("registrar debe guardar una anotación negativa")
    void testRegistrarNegativa() {
        // Arrange
        Anotacion anotacionNueva = new Anotacion();
        anotacionNueva.setEstudianteId(2L);
        anotacionNueva.setTipo("NEGATIVA");
        anotacionNueva.setDescripcion("No presenta la tarea de Lenguaje y Comunicación.");
        anotacionNueva.setFecha(LocalDate.of(2026, 5, 11));

        Anotacion anotacionGuardada = new Anotacion();
        anotacionGuardada.setId(2L);
        anotacionGuardada.setEstudianteId(2L);
        anotacionGuardada.setTipo("NEGATIVA");
        anotacionGuardada.setDescripcion("No presenta la tarea de Lenguaje y Comunicación.");
        anotacionGuardada.setFecha(LocalDate.of(2026, 5, 11));

        when(anotacionRepository.save(any(Anotacion.class))).thenReturn(anotacionGuardada);

        // Act
        Anotacion resultado = anotacionController.registrar(anotacionNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals("NEGATIVA", resultado.getTipo());
        verify(anotacionRepository, times(1)).save(anotacionNueva);
    }

    @Test
    @DisplayName("listarPorEstudiante debe retornar anotaciones del estudiante")
    void testListarPorEstudiante() {
        // Arrange
        Long estudianteId = 1L;
        Anotacion anotacion = new Anotacion();
        anotacion.setId(1L);
        anotacion.setEstudianteId(estudianteId);
        anotacion.setTipo("POSITIVA");
        anotacion.setDescripcion("Excelente participación en la feria científica.");
        anotacion.setFecha(LocalDate.of(2026, 5, 10));

        when(anotacionRepository.findByEstudianteId(estudianteId))
                .thenReturn(List.of(anotacion));

        // Act
        List<Anotacion> resultado = anotacionController.listarPorEstudiante(estudianteId);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("POSITIVA", resultado.get(0).getTipo());
        verify(anotacionRepository, times(1)).findByEstudianteId(estudianteId);
    }

    @Test
    @DisplayName("listarPorEstudiante debe retornar lista vacía si no hay anotaciones")
    void testListarPorEstudianteVacio() {
        // Arrange
        Long estudianteId = 99L;
        when(anotacionRepository.findByEstudianteId(estudianteId)).thenReturn(List.of());

        // Act
        List<Anotacion> resultado = anotacionController.listarPorEstudiante(estudianteId);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(anotacionRepository, times(1)).findByEstudianteId(estudianteId);
    }
}

