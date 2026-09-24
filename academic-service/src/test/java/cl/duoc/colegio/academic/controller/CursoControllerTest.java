package cl.duoc.colegio.academic.controller;

import cl.duoc.colegio.academic.model.Curso;
import cl.duoc.colegio.academic.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoControllerTest {

    @Mock
    private CursoRepository cursoRepository;

    private CursoController cursoController;

    @BeforeEach
    void setUp() {
        cursoController = new CursoController(cursoRepository);
    }

    @Test
    @DisplayName("listarTodos debe retornar todos los cursos")
    void testListarTodos() {
        // Arrange
        Curso curso1 = new Curso(1L, "1 Medio", "A", 2026);
        Curso curso2 = new Curso(2L, "1 Medio", "B", 2026);

        when(cursoRepository.findAll()).thenReturn(List.of(curso1, curso2));

        // Act
        List<Curso> resultado = cursoController.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("1 Medio", resultado.get(0).getNivel());
        assertEquals("A", resultado.get(0).getLetra());
        assertEquals(2026, resultado.get(0).getAño());
        assertEquals("B", resultado.get(1).getLetra());
        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("guardar debe guardar un curso correctamente")
    void testGuardar() {
        // Arrange
        Curso cursoNuevo = new Curso(null, "2 Medio", "A", 2026);
        Curso cursoGuardado = new Curso(3L, "2 Medio", "A", 2026);

        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoGuardado);

        // Act
        Curso resultado = cursoController.guardar(cursoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals("2 Medio", resultado.getNivel());
        assertEquals("A", resultado.getLetra());
        assertEquals(2026, resultado.getAño());
        verify(cursoRepository, times(1)).save(cursoNuevo);
    }

    @Test
    @DisplayName("editar debe actualizar un curso existente")
    void testEditarCursoExistente() {
        // Arrange
        Long cursoId = 1L;
        Curso cursoOriginal = new Curso(cursoId, "1 Medio", "A", 2026);
        Curso cursoActualizado = new Curso(null, "2 Medio", "B", 2026);
        Curso cursoResultado = new Curso(cursoId, "2 Medio", "B", 2026);

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(cursoOriginal));
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoResultado);

        // Act
        Curso resultado = cursoController.editar(cursoId, cursoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("2 Medio", resultado.getNivel());
        assertEquals("B", resultado.getLetra());
        assertEquals(cursoId, resultado.getId());
        verify(cursoRepository, times(1)).findById(cursoId);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("editar debe crear un nuevo curso si no existe")
    void testEditarCursoNoExistente() {
        // Arrange
        Long cursoId = 99L;
        Curso cursoNuevo = new Curso(null, "3 Medio", "C", 2026);
        Curso cursoCreado = new Curso(cursoId, "3 Medio", "C", 2026);

        when(cursoRepository.findById(cursoId)).thenReturn(Optional.empty());
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoCreado);

        // Act
        Curso resultado = cursoController.editar(cursoId, cursoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(cursoId, resultado.getId());
        assertEquals("3 Medio", resultado.getNivel());
        assertEquals("C", resultado.getLetra());
        verify(cursoRepository, times(1)).findById(cursoId);
        verify(cursoRepository, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("eliminar debe eliminar un curso por ID")
    void testEliminar() {
        // Arrange
        Long cursoId = 1L;

        // Act
        cursoController.eliminar(cursoId);

        // Assert
        verify(cursoRepository, times(1)).deleteById(cursoId);
    }
}
