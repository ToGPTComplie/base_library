package com.example.library.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import com.example.library.annotation.TaskLog;

@Aspect
@Component
@Slf4j
public class TaskLogAspect {
    @Around("@annotation(taskLog)")
    public Object around(ProceedingJoinPoint point, TaskLog taskLog) throws Throwable {
        String taskName = taskLog.value();
        if (taskName.isEmpty()) {
            taskName = point.getSignature().getName();
        }

        long start = System.currentTimeMillis();
        log.info(">>> 定时任务 [{}] 开始执行", taskName);

        try {
            Object result = point.proceed();
            long end = System.currentTimeMillis();
            log.info("<<< 定时任务 [{}] 执行成功，耗时: {} ms", taskName, (end - start));
            return result;
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            log.error("!!! 定时任务 [{}] 执行异常，耗时: {} ms", taskName, (end - start), e);
            return null; 
        }
    }
}
