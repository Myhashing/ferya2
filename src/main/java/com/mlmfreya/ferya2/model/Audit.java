package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sessionId;
    @Column(nullable = false)
    private String email;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String ipAddress;
    // getters and setters
}