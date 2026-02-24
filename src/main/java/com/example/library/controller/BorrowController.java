package com.example.library.controller;

import com.example.library.common.Result;
import com.example.library.entity.BorrowRecord;
import com.example.library.security.CustomUserDetails;
import com.example.library.service.borrow.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/{bookId}")
    public Result<String> borrowBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        borrowService.borrowBook(userDetails.getUserId(), bookId);
        return Result.success("借阅成功");
    }

    @PostMapping("/return/{bookId}")
    public Result<String> returnBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        borrowService.returnBook(userDetails.getUserId(), bookId);
        return Result.success("归还成功");
    }

    @PostMapping("/renew/{bookId}")
    public Result<String> renewBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        borrowService.renewBook(userDetails.getUserId(), bookId);
        return Result.success("续借成功");
    }

    @GetMapping("/my-borrowed-books")
    public Result<java.util.List<BorrowRecord>> listMyBorrowedBooks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(borrowService.listMyBorrowedBooks(userDetails.getUserId()));
    }
}