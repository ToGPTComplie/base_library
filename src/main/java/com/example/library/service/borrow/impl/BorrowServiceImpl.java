package com.example.library.service.borrow.impl;

import com.example.library.common.ResultCode;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.User;
import com.example.library.exception.CommonException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.borrow.BorrowService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private final static Integer RENEW_LIMIT = 1;

    @Override
    @Transactional
    public void borrowBook(Long userId, Long bookId) {
        if (userId == null || bookId == null) {
            throw new NullPointerException("userId or bookId cannot be null");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new CommonException(ResultCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CommonException(ResultCode.BOOK_NOT_FOUND));

        if (borrowRecordRepository.findByUserIdAndBookIdAndReturnDateIsNull(userId, bookId).isPresent()){
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

    @Override
    @Transactional
        public void returnBook(Long userId, Long bookId){
        BorrowRecord borrowRecord = getBorrowRecord(userId, bookId);

        borrowRecord.setReturnDate(LocalDateTime.now());
        
        if(LocalDateTime.now().isAfter(borrowRecord.getDueDate())){
            borrowRecord.setStatus(2);
        }else{
            borrowRecord.setStatus(1);
        }

        borrowRecordRepository.save(borrowRecord);

        if (bookRepository.incrementStock(bookId) == 0 ){
            throw new CommonException(ResultCode.FAILED);
        }
    }

    @Override
    @Transactional
    public void renewBook(Long userId, Long bookId) {

        BorrowRecord record = getBorrowRecord(userId, bookId);

        if (record.getRenewCount() >= RENEW_LIMIT) { 
            throw new CommonException(ResultCode.RENEW_LIMIT_EXCEEDED);
        }

        if (LocalDateTime.now().isAfter(record.getDueDate())) {
            throw new CommonException(ResultCode.BOOK_ALREADY_OVERDUE);
        }

        record.setDueDate(record.getDueDate().plusDays(30)); // 延长30天
        record.setRenewCount(record.getRenewCount() + 1);
        
        borrowRecordRepository.save(record);
    }

    private @NonNull BorrowRecord getBorrowRecord(Long userId, Long bookId) {
        if (userId == null || bookId == null) {
            throw new NullPointerException("userId or bookId cannot be null");
        }

        userRepository.findById(userId).orElseThrow(() -> new CommonException(ResultCode.USER_NOT_FOUND));

        bookRepository.findById(bookId).orElseThrow(() -> new CommonException(ResultCode.BOOK_NOT_FOUND));

        return borrowRecordRepository.findByUserIdAndBookIdAndReturnDateIsNull(userId, bookId)
                .orElseThrow(() -> new CommonException(ResultCode.THIS_BOOK_NOT_BORROWED));
    }

    @Override
    @Transactional
    public java.util.List<BorrowRecord> listMyBorrowedBooks(Long userId) {
        if (userId == null) {
            throw new NullPointerException("userId cannot be null");
        }
        return borrowRecordRepository.findByUserIdAndReturnDateIsNull(userId);
    }
}
