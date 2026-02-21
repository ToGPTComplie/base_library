package com.example.library.controller;

import java.util.List;

import com.example.library.common.Result;
import com.example.library.dto.BookAddRequest;
import com.example.library.dto.BookSearchResponse;
import com.example.library.service.book.BookService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("api/books")
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    Result<String> addBook(@RequestBody @Valid BookAddRequest bookAddRequest) {
        bookService.addBook(bookAddRequest);
        return Result.success();
    }

    @GetMapping("/search")
    Result<List<BookSearchResponse>> searchBook(@RequestParam(required = false) String keyword) {
        List<BookSearchResponse> bookSearchResponses = bookService.searchBooksByKeyword(keyword);
        return Result.success(bookSearchResponses);
    }
}
