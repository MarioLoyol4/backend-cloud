
package cl.duoc.colegio.dto;

import jakarta.validation.constraints.NotBlank;

public class QueueRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    public QueueRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
