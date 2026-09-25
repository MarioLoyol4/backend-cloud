package cl.duoc.colegio.bff.controller;

import cl.duoc.colegio.bff.security.IdentidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/bff")
@RequiredArgsConstructor
public class PerfilController {

    private final IdentidadService identidadService;

    @GetMapping("/perfil")
    public ResponseEntity<?> miPerfil(@AuthenticationPrincipal Jwt jwt) {
        var perfil = identidadService.resolverPerfil(jwt);
        return ResponseEntity.ok(Map.of(
                "referenciaId", perfil.referenciaId(),
                "rol", perfil.rol(),
                "estudiantesACargo", perfil.estudiantesACargo()
        ));
    }
}