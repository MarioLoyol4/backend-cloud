package cl.duoc.colegio.service;


import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
@RequiredArgsConstructor
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin; // Inyectado por Spring Boot

    public void createQueue(String name) {
        rabbitAdmin.declareQueue(new Queue(name, true));
    }
    public void deleteQueue(String name) {
        rabbitAdmin.deleteQueue(name);
    }
    // Métodos para crear/eliminar Exchanges y Bindings...
}

