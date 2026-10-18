package de.api_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.api_service.dto.AuthenticationResponse;
import de.api_service.dto.LoginRequest;
import de.api_service.dto.RegisterRequest;
import de.api_service.model.Role;
import de.api_service.model.User;
import de.api_service.repository.UserRepository;
import de.api_service.security.JwtService;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Self-registration always creates a regular USER; admins are created out of band. */
    public AuthenticationResponse register(RegisterRequest request) {
        String username = normalize(request.username());
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account with this username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setVorname(request.vorname());
        user.setNachname(request.nachname());
        user.setTelefonnummer(request.telefonnummer());
        user.setRole(Role.USER);
        userRepository.save(user);

        return toResponse(user);
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        String username = normalize(request.username());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password()));

        User user = userRepository.findByUsername(username).orElseThrow();
        return toResponse(user);
    }

    /** Usernames double as Telegram handles, which are case-insensitive and written with an optional '@'. */
    public static String normalize(String username) {
        String value = username.trim();
        if (value.startsWith("@")) {
            value = value.substring(1);
        }
        return value.toLowerCase();
    }

    private AuthenticationResponse toResponse(User user) {
        return new AuthenticationResponse(jwtService.generateToken(user), user.getUsername(), user.getRole());
    }
}
