package cl.duoc.colegio.attendance.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private ApiKeyFilter apiKeyFilter;

    @BeforeEach
    void setUp() {
        apiKeyFilter = new ApiKeyFilter();
        // Inyectamos el valor de la propiedad sin levantar Spring
        ReflectionTestUtils.setField(apiKeyFilter, "apiKey", "colegio-ohiggins-internal-2024");
    }

    @Test
    @DisplayName("debe permitir acceso a h2-console sin API Key")
    void testPermiteH2Console() throws Exception {
        when(request.getRequestURI()).thenReturn("/h2-console/login");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("debe rechazar peticion sin header API Key")
    void testRechazaSinHeader() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/asistencias");
        when(request.getHeader("X-Internal-Api-Key")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("debe rechazar peticion con API Key incorrecta")
    void testRechazaConHeaderIncorrecto() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/asistencias");
        when(request.getHeader("X-Internal-Api-Key")).thenReturn("clave-incorrecta");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("debe permitir peticion con API Key correcta")
    void testPermiteConHeaderCorrecto() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/asistencias");
        when(request.getHeader("X-Internal-Api-Key")).thenReturn("colegio-ohiggins-internal-2024");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }
}