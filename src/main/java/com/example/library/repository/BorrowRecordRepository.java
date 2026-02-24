package com.example.library.repository;


import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    // 查询用户当前未归还的特定书籍记录 (status = 0 或 2)
    // 用于检查是否重复借阅
    Optional<BorrowRecord> findByUserIdAndBookIdAndReturnDateIsNull(Long userId, Long bookId);

    // 查询用户所有未归还的记录
    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);

    @Modifying
    @Query("UPDATE BorrowRecord r SET r.status = 2 WHERE r.status = 0 AND r.dueDate < :now")
    int updateOverdueStatus(@Param("now") LocalDateTime now);
}
