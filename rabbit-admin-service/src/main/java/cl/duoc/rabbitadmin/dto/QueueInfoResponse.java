package cl.duoc.rabbitadmin.dto;

public record QueueInfoResponse(
        String name,
        boolean exists,
        int messages,
        int consumers
) {}
