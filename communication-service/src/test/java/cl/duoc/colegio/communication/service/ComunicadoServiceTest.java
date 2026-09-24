package cl.duoc.colegio.communication.service;

import cl.duoc.colegio.communication.model.Comunicado;
import cl.duoc.colegio.communication.repository.ComunicadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicadoServiceTest {

    @Mock
    private ComunicadoRepository comunicadoRepository;

    private ComunicadoService comunicadoService;

    @BeforeEach
    void setUp() {
        comunicadoService = new ComunicadoService(comunicadoRepository);
    }

    @Test
    @DisplayName("publicar debe guardar el comunicado y retornarlo")
    void testPublicar() {
        Comunicado comunicado = new Comunicado();
        comunicado.setTitulo("Reunion de apoderados");
        comunicado.setContenido("Viernes a las 19:00 hrs");
        comunicado.setAutorId("admin-1");
        comunicado.setDestinatario("APODERADOS");

        when(comunicadoRepository.save(comunicado)).thenReturn(comunicado);

        Comunicado resultado = comunicadoService.publicar(comunicado);

        assertNotNull(resultado);
        assertEquals("Reunion de apoderados", resultado.getTitulo());
        verify(comunicadoRepository, times(1)).save(comunicado);
    }

    @Test
    @DisplayName("getTodos debe retornar todos los comunicados")
    void testGetTodos() {
        when(comunicadoRepository.findAll())
                .thenReturn(List.of(new Comunicado(), new Comunicado()));

        List<Comunicado> resultado = comunicadoService.getTodos();

        assertEquals(2, resultado.size());
        verify(comunicadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getByDestinatario debe retornar comunicados filtrados")
    void testGetByDestinatario() {
        Comunicado comunicado = new Comunicado();
        comunicado.setDestinatario("APODERADOS");

        when(comunicadoRepository.findByDestinatario("APODERADOS"))
                .thenReturn(List.of(comunicado));

        List<Comunicado> resultado = comunicadoService.getByDestinatario("APODERADOS");

        assertEquals(1, resultado.size());
        assertEquals("APODERADOS", resultado.get(0).getDestinatario());
        verify(comunicadoRepository, times(1)).findByDestinatario("APODERADOS");
    }

    @Test
    @DisplayName("getByDestinatario debe retornar lista vacia si no hay comunicados")
    void testGetByDestinatarioVacio() {
        when(comunicadoRepository.findByDestinatario("ESTUDIANTES"))
                .thenReturn(List.of());

        List<Comunicado> resultado = comunicadoService.getByDestinatario("ESTUDIANTES");

        assertTrue(resultado.isEmpty());
    }
}