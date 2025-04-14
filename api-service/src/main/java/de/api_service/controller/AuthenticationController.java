package de.api_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.api_service.model.User;
import de.api_service.service.AuthenticationService;

@RestController
public class AuthenticationController {


    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public String register(@RequestBody User request){
        authenticationService.register(request);
        return "User is saved";
    }

    @PostMapping("/login")
    public String login(@RequestBody User request){
        authenticationService.authenticate(request);
        return "Successfully";
    }

}
