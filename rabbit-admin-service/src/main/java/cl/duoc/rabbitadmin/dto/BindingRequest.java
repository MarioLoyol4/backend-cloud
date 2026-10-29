package cl.duoc.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BindingRequest(
        @NotBlank(message = "La cola es obligatoria") String queue,
        @NotBlank(message = "El exchange es obligatorio") String exchange,
        @NotNull(message = "La routing key es obligatoria (puede ser vacía)")
        @Size(max = 255, message = "La routing key admite hasta 255 caracteres") String routingKey
) {}
