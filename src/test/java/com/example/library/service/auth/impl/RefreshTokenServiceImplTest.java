package com.example.library.service.auth.impl;

import com.example.library.dto.TokenRefreshResponse;
import com.example.library.entity.RefreshToken;
import com.example.library.entity.User;
import com.example.library.repository.RefreshTokenRepository;
import com.example.library.repository.UserRepository;
import com.example.library.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private final Long refreshTokenDurationMs = 60000L; // 1 minute

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Inject the @Value property
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", refreshTokenDurationMs);
    }

    @Test
    @DisplayName("deleteByUserId - should delete tokens for user")
    void deleteByUserId() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        int result = refreshTokenService.deleteByUserId(1L);

        // Assert
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    @DisplayName("createRefreshToken - should create token for user with deviceId")
    void createRefreshToken_Success() {
        // Arrange
        String deviceId = "device-123";
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(1L, deviceId);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(deviceId, result.getDeviceId());
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiryDate());
        
        verify(refreshTokenRepository).deleteByUserAndDeviceId(user, deviceId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("createRefreshToken - should create token for user without deviceId")
    void createRefreshToken_NoDeviceId() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(1L, null);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNull(result.getDeviceId());
        
        verify(refreshTokenRepository, never()).deleteByUserAndDeviceId(any(), any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("verifyExpiration - should throw exception when token expired")
    void verifyExpiration_Expired() {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setToken("expired-token");
        token.setExpiryDate(Instant.now().minusMillis(1000)); // Expired

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });

        assertTrue(exception.getMessage().contains("Refresh token expired"));
        verify(refreshTokenRepository).delete(token);
    }

    @Test
    @DisplayName("verifyExpiration - should return token when valid")
    void verifyExpiration_Valid() {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setToken("valid-token");
        token.setExpiryDate(Instant.now().plusMillis(1000)); // Valid

        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(token);

        // Assert
        assertEquals(token, result);
        verify(refreshTokenRepository, never()).delete(token);
    }

    @Test
    @DisplayName("refreshToken - should refresh token when device matches")
    void refreshToken_Success() {
        // Arrange
        String requestToken = "old-token";
        String deviceId = "device-123";
        
        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(user);
        oldToken.setToken(requestToken);
        oldToken.setDeviceId(deviceId);
        oldToken.setExpiryDate(Instant.now().plusMillis(10000));

        when(refreshTokenRepository.findByToken(requestToken)).thenReturn(Optional.of(oldToken));
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("new-access-token");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TokenRefreshResponse response = refreshTokenService.refreshToken(requestToken, deviceId);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        
        verify(refreshTokenRepository).delete(oldToken);
        // Should create new token with same deviceId
        verify(refreshTokenRepository).deleteByUserAndDeviceId(user, deviceId);
    }

    @Test
    @DisplayName("refreshToken - should fail when device mismatch")
    void refreshToken_DeviceMismatch() {
        // Arrange
        String requestToken = "old-token";
        String oldDeviceId = "device-123";
        String newDeviceId = "device-456"; // Different device trying to use the token
        
        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(user);
        oldToken.setToken(requestToken);
        oldToken.setDeviceId(oldDeviceId);
        oldToken.setExpiryDate(Instant.now().plusMillis(10000));

        when(refreshTokenRepository.findByToken(requestToken)).thenReturn(Optional.of(oldToken));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.refreshToken(requestToken, newDeviceId);
        });

        assertEquals("Refresh token device mismatch", exception.getMessage());
        verify(refreshTokenRepository, never()).delete(oldToken);
    }
    
    @Test
    @DisplayName("refreshToken - should succeed when deviceId provided but token has no deviceId (legacy token)")
    void refreshToken_LegacyToken_Upgrade() {
        // Arrange
        String requestToken = "old-legacy-token";
        String deviceId = "device-123";
        
        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(user);
        oldToken.setToken(requestToken);
        oldToken.setDeviceId(null); // Legacy token
        oldToken.setExpiryDate(Instant.now().plusMillis(10000));

        when(refreshTokenRepository.findByToken(requestToken)).thenReturn(Optional.of(oldToken));
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("new-access-token");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TokenRefreshResponse response = refreshTokenService.refreshToken(requestToken, deviceId);

        // Assert
        assertNotNull(response);
        
        // Old token should be deleted
        verify(refreshTokenRepository).delete(oldToken);
        // New token should be created WITH the new deviceId
        verify(refreshTokenRepository).deleteByUserAndDeviceId(user, deviceId);
    }
}
