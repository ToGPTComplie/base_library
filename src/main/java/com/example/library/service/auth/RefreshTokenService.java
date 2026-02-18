package com.example.library.service.auth;

import com.example.library.dto.TokenRefreshResponse;
import com.example.library.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    public Optional<RefreshToken> findByToken(String token);

    public RefreshToken createRefreshToken(Long userId, String deviceId);

    public RefreshToken verifyExpiration(RefreshToken token);

    public int deleteByUserId(Long userId);

    TokenRefreshResponse refreshToken(String requestRefreshToken, String deviceId);
}
