package com.example.library.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DeviceIdResolver {
    public String resolveDeviceId(HttpServletRequest request) {
        String deviceId = request.getHeader("X-Device-Id");
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = request.getHeader("User-Agent");
        }
        return deviceId;
    }
}
