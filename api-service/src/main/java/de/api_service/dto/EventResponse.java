package de.api_service.dto;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String topic,
        String content,
        LocalDateTime dueDate,
        Long userId,
        String username) {
}
