package de.api_service.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EventRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 255) String topic,
        String content,
        @NotNull LocalDateTime dueDate,
        @NotNull Long userId) {
}
