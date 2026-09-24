package cl.duoc.colegio.bff.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter();
        ReflectionTestUtils.setField(jwtFilter, "jwtUtil", jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("debe continuar sin autenticar si no hay header Authorization")
    void testSinHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("debe continuar sin autenticar si el header no empieza con Bearer")
    void testHeaderSinBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic token123");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("debe autenticar si el token es valido")
    void testTokenValido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtUtil.validarToken("token-valido")).thenReturn(true);
        when(jwtUtil.extraerUserId("token-valido")).thenReturn("apoderado-1");
        when(jwtUtil.extraerRol("token-valido")).thenReturn("APODERADO");
        when(jwtUtil.extraerEstudiantesACargo("token-valido")).thenReturn(List.of(1L, 2L));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("apoderado-1",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    @DisplayName("debe continuar sin autenticar si el token es invalido")
    void testTokenInvalido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(jwtUtil.validarToken("token-invalido")).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("debe setear el rol correcto en la autenticacion")
    void testRolCorrecto() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-admin");
        when(jwtUtil.validarToken("token-admin")).thenReturn(true);
        when(jwtUtil.extraerUserId("token-admin")).thenReturn("admin-1");
        when(jwtUtil.extraerRol("token-admin")).thenReturn("ADMIN");
        when(jwtUtil.extraerEstudiantesACargo("token-admin")).thenReturn(List.of());

        jwtFilter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}