package cl.duoc.colegio.bff.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "clave-super-secreta-del-colegio-ohiggins-2024-suficientemente-larga");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    @DisplayName("generarToken debe crear un token no nulo")
    void testGenerarToken() {
        String token = jwtUtil.generarToken("admin-1", "ADMIN", List.of());

        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT tiene 3 partes separadas por punto
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("extraerUserId debe retornar el id del usuario")
    void testExtraerUserId() {
        String token = jwtUtil.generarToken("apoderado-1", "APODERADO", List.of(1L, 2L));

        String userId = jwtUtil.extraerUserId(token);

        assertEquals("apoderado-1", userId);
    }

    @Test
    @DisplayName("extraerRol debe retornar el rol del token")
    void testExtraerRol() {
        String token = jwtUtil.generarToken("docente-1", "DOCENTE", List.of());

        String rol = jwtUtil.extraerRol(token);

        assertEquals("DOCENTE", rol);
    }

    @Test
    @DisplayName("extraerEstudiantesACargo debe retornar la lista del token")
    void testExtraerEstudiantesACargo() {
        String token = jwtUtil.generarToken("apoderado-1", "APODERADO", List.of(1L, 2L, 3L));

        List<Long> estudiantes = jwtUtil.extraerEstudiantesACargo(token);

        assertNotNull(estudiantes);
        assertEquals(3, estudiantes.size());
    }

    @Test
    @DisplayName("validarToken debe retornar true para un token valido")
    void testValidarTokenValido() {
        String token = jwtUtil.generarToken("admin-1", "ADMIN", List.of());

        assertTrue(jwtUtil.validarToken(token));
    }

    @Test
    @DisplayName("validarToken debe retornar false para un token invalido")
    void testValidarTokenInvalido() {
        assertFalse(jwtUtil.validarToken("token.invalido.xxx"));
    }

    @Test
    @DisplayName("validarToken debe retornar false para un token manipulado")
    void testValidarTokenManipulado() {
        String token = jwtUtil.generarToken("admin-1", "ADMIN", List.of());
        String tokenManipulado = token + "manipulado";

        assertFalse(jwtUtil.validarToken(tokenManipulado));
    }

    @Test
    @DisplayName("tokens de distintos roles deben tener roles diferentes")
    void testDistintosRoles() {
        String tokenAdmin = jwtUtil.generarToken("admin-1", "ADMIN", List.of());
        String tokenDocente = jwtUtil.generarToken("docente-1", "DOCENTE", List.of());

        assertEquals("ADMIN", jwtUtil.extraerRol(tokenAdmin));
        assertEquals("DOCENTE", jwtUtil.extraerRol(tokenDocente));
    }
}