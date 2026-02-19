package com.example.library.service.auth.impl;

import com.example.library.common.Result;
import com.example.library.dto.TokenRefreshResponse;
import com.example.library.entity.RefreshToken;
import com.example.library.entity.User;
import com.example.library.repository.RefreshTokenRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.auth.RefreshTokenService;
import com.example.library.util.JwtUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId, String deviceId) {
        RefreshToken refreshToken = new RefreshToken();
        return userRepository.findById(userId).map(user ->
            {
                if (deviceId != null && !deviceId.isBlank()) {
                    refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
                    refreshTokenRepository.flush(); // Force delete to execute before insert
                }
                refreshToken.setUser(user);
                refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
                refreshToken.setToken(UUID.randomUUID().toString());
                refreshToken.setDeviceId(deviceId);
                return refreshTokenRepository.save(refreshToken);
            }
        ).orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + "Refresh token expired");
        }
        return token;
    }

    @Override
    public int deleteByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(refreshTokenRepository::deleteByUser)
                .orElse(0);
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(String requestRefreshToken, String deviceId) {
        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(oldToken -> {
                    User user = oldToken.getUser();
                    if (deviceId != null && !deviceId.isBlank()) {
                        // 仅当旧 Token 已绑定设备且与当前设备不一致时，才抛出异常
                        if (oldToken.getDeviceId() != null && !oldToken.getDeviceId().equals(deviceId)) {
                            throw new RuntimeException("Refresh token device mismatch");
                        }
                    }
                    String accessToken = jwtUtil.generateToken(user.getUsername());
                    String resolvedDeviceId = deviceId != null && !deviceId.isBlank() ? deviceId : oldToken.getDeviceId();
                    String refreshToken = createRefreshToken(user.getId(), resolvedDeviceId).getToken();
                    refreshTokenRepository.delete(oldToken);
                    return new TokenRefreshResponse(accessToken, refreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}
