package de.api_service.dto;

import de.api_service.model.Role;

public record AuthenticationResponse(String token, String username, Role role) {
}
