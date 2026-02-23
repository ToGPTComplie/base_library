package com.example.library.repository;

import com.example.library.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByIsbnContaining(String isbn);

    List<Book> findByBookTitleContaining(String bookTitle);

    List<Book> findByAuthorContaining(String author);

    @Query("SELECT b FROM Book b WHERE b.bookTitle LIKE %:keyword% OR b.author LIKE %:keyword%")
    List<Book> findByBookTitleContainingOrAuthorContaining(@Param("keyword") String keyword);

    @Modifying
    @Query("UPDATE Book b SET b.availableStock = b.availableStock - 1 WHERE b.id = :id AND b.availableStock > 0")
    int decrementStock(@Param("id") Long id);
}
