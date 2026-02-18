package com.example.library.repository;

import com.example.library.entity.RefreshToken;
import com.example.library.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserAndDeviceId(User user, String deviceId);

    @Modifying
    int deleteByUser(User user);

    @Modifying
    int deleteByUserAndDeviceId(User user, String deviceId);
}
