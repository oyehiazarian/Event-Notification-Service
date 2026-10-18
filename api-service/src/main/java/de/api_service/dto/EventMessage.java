package de.api_service.dto;

import java.time.LocalDateTime;

/** Payload published to Kafka; the record key is the recipient's username. */
public record EventMessage(
        Long id,
        String title,
        String topic,
        String content,
        LocalDateTime dueDate,
        String username) {
}
