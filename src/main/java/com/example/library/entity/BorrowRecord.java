package com.example.library.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "borrow_records")
@Data
@NoArgsConstructor
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    // 应还时间 (通常是借出时间 + 30天)
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    // 实还时间 (为空表示未还)
    @Column(name = "return_date")
    private LocalDateTime returnDate;

    // 状态: 0-借出中, 1-已还, 2-逾期
    @Column(name = "status", nullable = false)
    private Integer status;
}
