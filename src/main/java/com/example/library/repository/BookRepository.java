package com.example.library.repository;

import com.example.library.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByIsbnContaining(String isbn);

    List<Book> findByBookTitleContaining(String bookTitle);

    List<Book> findByAuthorContaining(String author);

    List<Book> findByBookTitleContainingOrAuthorContaining(String bookTitle, String author);
}
