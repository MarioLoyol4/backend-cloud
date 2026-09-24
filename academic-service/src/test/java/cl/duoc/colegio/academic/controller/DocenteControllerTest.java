package cl.duoc.colegio.academic.controller;


import cl.duoc.colegio.academic.model.Asignatura;
import cl.duoc.colegio.academic.model.Docente;
import cl.duoc.colegio.academic.repository.AsignaturaRepository;
import cl.duoc.colegio.academic.repository.DocenteRepository;
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
class DocenteControllerTest {
    @Mock
    private DocenteRepository docenteRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DocenteController docenteController;

    @BeforeEach
    void setUp() {
        docenteController = new DocenteController(docenteRepository, asignaturaRepository, passwordEncoder);
    }

    @Test
    @DisplayName("crear debe encriptar la contraseña antes de guardar")
    void testCrear() {
        // Arrange
        Docente docenteNuevo = new Docente();
        docenteNuevo.setRut("11111111-1");
        docenteNuevo.setNombre("Roberto");
        docenteNuevo.setApellido("Gómez");
        docenteNuevo.setEmail("roberto@colegio.cl");
        docenteNuevo.setTelefono("+56911111111");
        docenteNuevo.setPassword("docente123");

        Docente docenteGuardado = new Docente();
        docenteGuardado.setId(1L);
        docenteGuardado.setRut("11111111-1");
        docenteGuardado.setNombre("Roberto");
        docenteGuardado.setApellido("Gómez");
        docenteGuardado.setEmail("roberto@colegio.cl");
        docenteGuardado.setPassword("hash-encriptado");

        when(passwordEncoder.encode("docente123")).thenReturn("hash-encriptado");
        when(docenteRepository.save(any(Docente.class))).thenReturn(docenteGuardado);

        // Act
        ResponseEntity<Docente> respuesta = docenteController.crear(docenteNuevo);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("hash-encriptado", respuesta.getBody().getPassword());
        verify(passwordEncoder, times(1)).encode("docente123");
        verify(docenteRepository, times(1)).save(any(Docente.class));
    }

    @Test
    @DisplayName("obtenerTodos debe retornar todos los docentes")
    void testObtenerTodos() {
        // Arrange
        Docente docente1 = new Docente();
        docente1.setId(1L);
        docente1.setNombre("Roberto");
        docente1.setApellido("Gómez");

        Docente docente2 = new Docente();
        docente2.setId(2L);
        docente2.setNombre("Camila");
        docente2.setApellido("Soto");

        when(docenteRepository.findAll()).thenReturn(List.of(docente1, docente2));

        // Act
        ResponseEntity<List<Docente>> respuesta = docenteController.obtenerTodos();

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().size());
        assertEquals("Roberto", respuesta.getBody().get(0).getNombre());
        assertEquals("Camila", respuesta.getBody().get(1).getNombre());
        verify(docenteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId debe retornar un docente existente")
    void testObtenerPorIdExistente() {
        // Arrange
        Long docenteId = 1L;
        Docente docente = new Docente();
        docente.setId(docenteId);
        docente.setNombre("Roberto");
        docente.setApellido("Gómez");

        when(docenteRepository.findById(docenteId)).thenReturn(Optional.of(docente));

        // Act
        ResponseEntity<Docente> respuesta = docenteController.obtenerPorId(docenteId);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Roberto", respuesta.getBody().getNombre());
        verify(docenteRepository, times(1)).findById(docenteId);
    }

    @Test
    @DisplayName("obtenerPorId debe retornar 404 si el docente no existe")
    void testObtenerPorIdNoExistente() {
        // Arrange
        Long docenteId = 99L;

        when(docenteRepository.findById(docenteId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Docente> respuesta = docenteController.obtenerPorId(docenteId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(docenteRepository, times(1)).findById(docenteId);
    }

    @Test
    @DisplayName("asignarAsignatura debe agregar una asignatura al docente")
    void testAsignarAsignatura() {
        // Arrange
        Long docenteId = 1L;
        Long asignaturaId = 1L;

        Docente docente = new Docente();
        docente.setId(docenteId);
        docente.setNombre("Roberto");
        docente.setAsignaturas(new ArrayList<>());

        Asignatura asignatura = new Asignatura();
        asignatura.setId(asignaturaId);
        asignatura.setNombre("Matemáticas");

        Docente docenteConAsignatura = new Docente();
        docenteConAsignatura.setId(docenteId);
        docenteConAsignatura.setNombre("Roberto");
        docenteConAsignatura.setAsignaturas(List.of(asignatura));

        when(docenteRepository.findById(docenteId)).thenReturn(Optional.of(docente));
        when(asignaturaRepository.findById(asignaturaId)).thenReturn(Optional.of(asignatura));
        when(docenteRepository.save(any(Docente.class))).thenReturn(docenteConAsignatura);

        // Act
        ResponseEntity<Docente> respuesta = docenteController.asignarAsignatura(docenteId, asignaturaId);

        // Assert
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().getAsignaturas().size());
        assertEquals("Matemáticas", respuesta.getBody().getAsignaturas().get(0).getNombre());
        verify(docenteRepository, times(1)).findById(docenteId);
        verify(asignaturaRepository, times(1)).findById(asignaturaId);
        verify(docenteRepository, times(1)).save(any(Docente.class));
    }

    @Test
    @DisplayName("asignarAsignatura debe lanzar excepción si docente no existe")
    void testAsignarAsignaturaDocenteNoExistente() {
        // Arrange
        Long docenteId = 99L;
        Long asignaturaId = 1L;

        when(docenteRepository.findById(docenteId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> docenteController.asignarAsignatura(docenteId, asignaturaId));

        verify(docenteRepository, times(1)).findById(docenteId);
        verify(asignaturaRepository, never()).findById(any());
        verify(docenteRepository, never()).save(any());
    }

    @Test
    @DisplayName("asignarAsignatura debe lanzar excepción si asignatura no existe")
    void testAsignarAsignaturaNoExistente() {
        // Arrange
        Long docenteId = 1L;
        Long asignaturaId = 99L;

        Docente docente = new Docente();
        docente.setId(docenteId);
        docente.setNombre("Roberto");

        when(docenteRepository.findById(docenteId)).thenReturn(Optional.of(docente));
        when(asignaturaRepository.findById(asignaturaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> docenteController.asignarAsignatura(docenteId, asignaturaId));

        verify(docenteRepository, times(1)).findById(docenteId);
        verify(asignaturaRepository, times(1)).findById(asignaturaId);
        verify(docenteRepository, never()).save(any());
    }

    @Test
    @DisplayName("eliminar debe eliminar un docente por ID")
    void testEliminar() {
        // Arrange
        Long docenteId = 1L;

        // Act
        ResponseEntity<Void> respuesta = docenteController.eliminar(docenteId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(docenteRepository, times(1)).deleteById(docenteId);
    }
}

