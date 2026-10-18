package de.bot_consumer.notification;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Payload published by api-service to the events topic. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EventMessage(
        Long id,
        String title,
        String topic,
        String content,
        LocalDateTime dueDate,
        String username) {
}
