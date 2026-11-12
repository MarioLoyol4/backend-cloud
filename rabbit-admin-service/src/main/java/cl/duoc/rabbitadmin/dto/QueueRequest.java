package cl.duoc.rabbitadmin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record QueueRequest(
        @NotBlank(message = "El nombre de la cola es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9._-]{1,255}$", message = "El nombre solo admite letras, números, '.', '_' y '-' (máximo 255)")
        String name,
        Boolean durable,
        @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "Dead-letter exchange inválido")
        String deadLetterExchange,
        @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "Dead-letter routing key inválida")
        String deadLetterRoutingKey,
        @Min(value = 1, message = "El TTL debe ser mayor a 0")
        @Max(value = 86400000, message = "El TTL máximo es 24 horas")
        Long ttlMs,
        @Min(value = 1, message = "El largo máximo debe ser mayor a 0")
        Integer maxLength
) {}
