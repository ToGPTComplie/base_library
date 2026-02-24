package com.example.library.service.borrow;

import com.example.library.entity.BorrowRecord;

public interface BorrowService {
    public void borrowBook(Long userId, Long bookId);

    public void returnBook(Long userId, Long bookId);
    
    public void renewBook(Long userId, Long bookId);

    public java.util.List<BorrowRecord> listMyBorrowedBooks(Long userId);
}
