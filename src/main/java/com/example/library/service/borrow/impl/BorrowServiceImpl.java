package com.example.library.service.borrow.impl;

import com.example.library.common.ResultCode;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.User;
import com.example.library.exception.AuthException;
import com.example.library.exception.CommonException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.borrow.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void borrowBook(Long userId, Long bookId) {
        if (userId == null || bookId == null) {
            throw new NullPointerException("userId and bookId cannot be null");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ResultCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CommonException(ResultCode.BOOK_NOT_FOUND));

        if (!borrowRecordRepository.findByUserIdAndBookIdAndReturnDateIsNull(userId, bookId).isEmpty()){
            throw new CommonException(ResultCode.THIS_BOOK_ALREADY_BORROWED);
        }

        if(borrowRecordRepository.findByUserIdAndReturnDateIsNull(userId).size()>10){
            throw new CommonException(ResultCode.YOU_CANT_BORROW_MORE);
        }

        int updatedRows = bookRepository.decrementStock(bookId);
        if (updatedRows == 0) {
            throw new CommonException(ResultCode.BOOK_AVAILABLE_STOCK_NOT_ENOUGH);
        }

        BorrowRecord borrowRecord = new BorrowRecord();

        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        borrowRecord.setDueDate(LocalDateTime.now().plusDays(30));
        borrowRecord.setStatus(0);
        
        borrowRecordRepository.save(borrowRecord);
    }
}
