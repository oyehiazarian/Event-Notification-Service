package de.api_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import de.api_service.model.Role;
import de.api_service.model.User;
import de.api_service.repository.UserRepository;
import de.api_service.service.AuthenticationService;

/** Creates the first administrator from ADMIN_USERNAME / ADMIN_PASSWORD when they are set. */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder,
            @Value("${app.admin.username:}") String username,
            @Value("${app.admin.password:}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (username.isBlank() || password.isBlank()) {
            return;
        }
        String normalized = AuthenticationService.normalize(username);
        if (userRepository.findByUsername(normalized).isPresent()) {
            return;
        }

        User admin = new User();
        admin.setUsername(normalized);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setVorname("Admin");
        admin.setNachname("Admin");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        LOG.info("Created initial admin user '{}'", normalized);
    }
}
