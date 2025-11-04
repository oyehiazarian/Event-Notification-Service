package de.api_service.model;

public class AuthenticationResponse {

    private final String token;

    public AuthenticationResponse(String token){
        this.token= token;
    }

    public String getToken(){
        return token;
    }
}
