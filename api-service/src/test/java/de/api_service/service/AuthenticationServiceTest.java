package de.api_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import de.api_service.dto.AuthenticationResponse;
import de.api_service.dto.RegisterRequest;
import de.api_service.model.Role;
import de.api_service.model.User;
import de.api_service.repository.UserRepository;
import de.api_service.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtService jwtService;
    @InjectMocks
    AuthenticationService service;

    @Test
    void registerAlwaysCreatesRegularUserWithNormalizedName() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("hash");
        when(jwtService.generateToken(any())).thenReturn("jwt");

        AuthenticationResponse response = service.register(
                new RegisterRequest("  @Alice ", "secret123", "A", "B", null));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Role.USER, captor.getValue().getRole());
        assertEquals("alice", captor.getValue().getUsername());
        assertEquals("hash", captor.getValue().getPassword());
        assertEquals("jwt", response.token());
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.register(new RegisterRequest("Alice", "secret123", "A", "B", null)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }
}
