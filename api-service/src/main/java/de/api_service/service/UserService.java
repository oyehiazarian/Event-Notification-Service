package de.api_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import de.api_service.dto.UserSummary;
import de.api_service.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername(), u.getVorname(), u.getNachname()))
                .toList();
    }
}
