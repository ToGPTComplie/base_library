package com.example.library.controller;

import com.example.library.common.Result;
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
}