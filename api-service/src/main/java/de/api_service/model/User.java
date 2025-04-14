package de.api_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usersecurity")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "vorname")
    private String Vorname;

    @Column(name = "nachname")
    private String Nachname;

    @Column(name = "telefonnummer")
    private int  telefonnummer;

    @Column(name = "role", nullable = false)
    private String role; // Роль пользователя (например, "user", "admin")

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp // Автоматически устанавливает время создания
    private LocalDateTime createdAt;

}
