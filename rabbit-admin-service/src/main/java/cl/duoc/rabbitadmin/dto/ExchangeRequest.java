package cl.duoc.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ExchangeRequest(
        @NotBlank(message = "El nombre del exchange es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9._-]{1,255}$", message = "Nombre de exchange inválido")
        String name,
        @NotNull(message = "El tipo es obligatorio (DIRECT, TOPIC, FANOUT o HEADERS)")
        ExchangeType type,
        Boolean durable
) {}
