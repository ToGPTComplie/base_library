package com.example.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "users_archive")
@Data
@NoArgsConstructor
public class UserArchive {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 64)
    @Column(name = "username", unique = true, nullable = false, length = 64)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "mobile", unique = true, length = 11)
    private String mobile;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "status", nullable = false, length = 1)
    private int status = 0;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    private String role = "user";
}
