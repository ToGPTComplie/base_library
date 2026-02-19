package com.example.library.entity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "device_id"}, name = "UK_user_device")
})
@Data
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "device_id")
    private String deviceId;

    @Column(nullable = false)
    private Instant expiryDate;
}
