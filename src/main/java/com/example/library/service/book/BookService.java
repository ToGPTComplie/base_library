package com.example.library.service.book;

import com.example.library.dto.BookAddRequest;
import com.example.library.dto.BookSearchResponse;

import java.util.List;


public interface BookService {
    void addBook(BookAddRequest bookAddRequest);

    List<BookSearchResponse> searchBooksByKeyword(String keyword);

    BookSearchResponse searchBookByIsbn(String isbn);
}
