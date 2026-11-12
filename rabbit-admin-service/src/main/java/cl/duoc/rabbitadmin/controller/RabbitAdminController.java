package cl.duoc.rabbitadmin.controller;

import cl.duoc.rabbitadmin.dto.*;
import cl.duoc.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rabbit")
@Validated
@RequiredArgsConstructor
public class RabbitAdminController {

    private final RabbitAdminService service;

    @PostMapping("/queues")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> crearCola(@Valid @RequestBody QueueRequest request) {
        service.crearCola(request);
        return Map.of("mensaje", "Cola " + request.name() + " creada");
    }

    @GetMapping("/queues")
    public List<QueueInfoResponse> colasDelSistema() {
        return service.colasDelSistema();
    }

    @GetMapping("/queues/{name}")
    public QueueInfoResponse infoCola(@PathVariable String name) {
        return service.infoCola(name);
    }

    @DeleteMapping("/queues/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCola(@PathVariable String name) {
        service.eliminarCola(name);
    }

    @PostMapping("/queues/{name}/purge")
    public Map<String, Object> purgar(@PathVariable String name) {
        return Map.of("mensajesEliminados", service.purgarCola(name));
    }

    @PostMapping("/exchanges")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> crearExchange(@Valid @RequestBody ExchangeRequest request) {
        service.crearExchange(request);
        return Map.of("mensaje", "Exchange " + request.name() + " creado");
    }

    @DeleteMapping("/exchanges/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarExchange(@PathVariable String name) {
        service.eliminarExchange(name);
    }

    @PostMapping("/bindings")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> crearBinding(@Valid @RequestBody BindingRequest request) {
        service.crearBinding(request);
        return Map.of("mensaje", "Binding creado");
    }

    @DeleteMapping("/bindings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarBinding(@RequestParam @NotBlank String queue,
                                @RequestParam @NotBlank String exchange,
                                @RequestParam(defaultValue = "") String routingKey) {
        service.eliminarBinding(queue, exchange, routingKey);
    }
}
