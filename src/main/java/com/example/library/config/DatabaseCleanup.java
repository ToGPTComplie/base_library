package com.example.library.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DatabaseCleanup {

    @Bean
    public CommandLineRunner manageConstraints(JdbcTemplate jdbcTemplate) {
        return args -> {
            // 1. 删除旧的 user_id 唯一约束 (UK7tdcd6ab5wsgoudnvj7xf1b7l)
            try {
                log.info("Attempting to drop stale unique constraint UK7tdcd6ab5wsgoudnvj7xf1b7l on refresh_tokens table...");
                jdbcTemplate.execute("ALTER TABLE refresh_tokens DROP INDEX UK7tdcd6ab5wsgoudnvj7xf1b7l");
                log.info("Successfully dropped stale constraint UK7tdcd6ab5wsgoudnvj7xf1b7l");
            } catch (DataAccessException e) {
                log.warn("Could not drop constraint UK7tdcd6ab5wsgoudnvj7xf1b7l (it might not exist or already dropped): {}", e.getMessage());
            }

            // 2. 添加新的联合唯一约束 (user_id, device_id)
            try {
                log.info("Attempting to add unique constraint UK_user_device on refresh_tokens table...");
                // 先尝试创建索引，如果数据有重复会失败
                jdbcTemplate.execute("ALTER TABLE refresh_tokens ADD CONSTRAINT UK_user_device UNIQUE (user_id, device_id)");
                log.info("Successfully added unique constraint UK_user_device");
            } catch (DataAccessException e) {
                // 如果约束已存在或数据冲突，会抛出异常
                if (e.getMessage().contains("Duplicate key name")) {
                     log.info("Constraint UK_user_device already exists.");
                } else {
                    log.error("Failed to add constraint UK_user_device: {}", e.getMessage());
                }
            }
        };
    }
}
