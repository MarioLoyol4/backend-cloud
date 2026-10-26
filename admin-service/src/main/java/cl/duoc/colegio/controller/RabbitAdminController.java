package cl.duoc.colegio.controller;


import cl.duoc.colegio.dto.QueueRequest;
import cl.duoc.colegio.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rabbit")
public class RabbitAdminController {

    private final RabbitAdminService adminService;

    public RabbitAdminController(RabbitAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<String> createQueue(@Valid @RequestBody QueueRequest request) {
        adminService.createQueue(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body("Cola creada");
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<String> deleteQueue(@PathVariable String name) {
        adminService.deleteQueue(name);
        return ResponseEntity.ok("Cola eliminada");
    }
}



