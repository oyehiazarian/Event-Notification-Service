package de.api_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.api_service.dto.UserSummary;
import de.api_service.service.UserService;

/** Admin only, see {@code SecurityConfig}. */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserSummary> users() {
        return userService.listUsers();
    }
}
