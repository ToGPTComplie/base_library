package com.example.library.task;

import com.example.library.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueCheckTask {

    private final BorrowRecordRepository borrowRecordRepository;

    // 每天凌晨 1:00 执行
    // cron 表达式: 秒 分 时 日 月 周
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    @TaskLog("检查逾期记录")
    public void checkOverdue() {
        int count = borrowRecordRepository.updateOverdueStatus(LocalDateTime.now());
    }
}