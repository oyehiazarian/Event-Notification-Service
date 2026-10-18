package de.api_service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.api_service.model.Role;
import de.api_service.model.User;
import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    // 48 random bytes, base64
    private static final String SECRET = "bXktdGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdW5pdC10ZXN0cy0xMjM0NTY3OA==";

    private static User user(String name) {
        User user = new User();
        user.setUsername(name);
        user.setRole(Role.USER);
        return user;
    }

    @Test
    void tokenRoundTrip() {
        JwtService jwt = new JwtService(SECRET, 60_000);
        User alice = user("alice");

        String token = jwt.generateToken(alice);

        assertEquals("alice", jwt.extractUsername(token));
        assertTrue(jwt.isValid(token, alice));
        assertFalse(jwt.isValid(token, user("bob")));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwt = new JwtService(SECRET, -1_000);
        String token = jwt.generateToken(user("alice"));

        assertThrows(JwtException.class, () -> jwt.isValid(token, user("alice")));
    }

    @Test
    void tokenSignedWithOtherKeyIsRejected() {
        String otherSecret = "b3RoZXItc2VjcmV0LWtleS1mb3Itand0LXVuaXQtdGVzdHMtYWJjZGVmZ2hpams=";
        String token = new JwtService(otherSecret, 60_000).generateToken(user("alice"));

        assertThrows(JwtException.class, () -> new JwtService(SECRET, 60_000).extractUsername(token));
    }
}
