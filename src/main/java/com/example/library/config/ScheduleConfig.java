// c:\Users\cfl\IdeaProjects\library\src\main\java\com\example\library\config\ScheduleConfig.java

package com.example.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduleConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 设置核心线程数，建议根据实际任务量配置，这里设为 5
        scheduler.setPoolSize(5);
        // 设置线程名前缀，方便排查日志
        scheduler.setThreadNamePrefix("scheduled-task-");
        // 设置等待所有任务完成后再关闭线程池（优雅停机）
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 设置关闭等待时间（秒）
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }
}