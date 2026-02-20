package com.example.library.service.book.impl;

import com.example.library.common.ResultCode;
import com.example.library.dto.BookAddRequest;
import com.example.library.dto.BookSearchResponse;
import com.example.library.entity.Book;
import com.example.library.exception.CommonException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookRepository;
import com.example.library.service.book.BookService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public void addBook(BookAddRequest bookAddRequest) {
        if (bookAddRequest == null) {
            throw new IllegalArgumentException("BookAddRequest cannot be null");
        }
        if ( bookAddRequest.getIsbn() == null || bookAddRequest.getIsbn().isBlank()) {
            throw new IllegalArgumentException("ISBN cannot be null or blank");
        }

        if (bookRepository.existsByIsbn(bookAddRequest.getIsbn())) {
            throw new CommonException(ResultCode.BOOK_ALREADY_EXIST);
        }

        Book book = bookMapper.toEntity(bookAddRequest);
        bookRepository.save(book);
    }

    @Override
    public List<BookSearchResponse> searchBooksByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return bookRepository.findByBookTitleContainingOrAuthorContaining(keyword, keyword).stream().map(bookMapper::toBookSearchResponse).toList();
    }

    @Override
    public BookSearchResponse searchBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }
        return bookRepository.findByIsbn(isbn).map(bookMapper::toBookSearchResponse).orElse(null);
    }

}
