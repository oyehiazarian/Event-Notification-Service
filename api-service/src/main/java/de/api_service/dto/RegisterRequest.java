package de.api_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 64) String username,
        @NotBlank @Size(min = 8, max = 128) String password,
        @NotBlank @Size(max = 255) String vorname,
        @NotBlank @Size(max = 255) String nachname,
        @Size(max = 255) String telefonnummer) {
}
