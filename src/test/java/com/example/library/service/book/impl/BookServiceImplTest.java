package com.example.library.service.book.impl;

import com.example.library.common.ResultCode;
import com.example.library.dto.BookAddRequest;
import com.example.library.dto.BookSearchResponse;
import com.example.library.entity.Book;
import com.example.library.exception.CommonException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addBook_FailBecauseOfNullIsbn() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> bookService.addBook(null));
        assertEquals("BookAddRequest cannot be null", e.getMessage());
    }

    @Test
    void addBook_FailBecauseOfEmptyIsbn() {
        BookAddRequest bookAddRequest = new BookAddRequest();
        Exception e = assertThrows(IllegalArgumentException.class, () -> bookService.addBook(bookAddRequest) );
        assertEquals("ISBN cannot be null or blank", e.getMessage());
    }

    @Test
    void addBook_FailBecauseOfBlankIsbn() {
        BookAddRequest bookAddRequest = new BookAddRequest();
        bookAddRequest.setIsbn("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> bookService.addBook(bookAddRequest) );
        assertEquals("ISBN cannot be null or blank", e.getMessage());
    }

    @Test
    void addBook_FailBeacauseOfBookAlreadyExists() {
        BookAddRequest bookAddRequest = new BookAddRequest();
        bookAddRequest.setIsbn("12345");

        when(bookRepository.existsByIsbn("12345")).thenReturn(true);

        CommonException e = assertThrows(CommonException.class, () -> bookService.addBook(bookAddRequest) );
        assertEquals(ResultCode.BOOK_ALREADY_EXIST, e.getResultCode());

    }

    @Test
    void addBook_Success() {
        BookAddRequest bookAddRequest = new BookAddRequest();
        bookAddRequest.setIsbn("978-7-111");
        bookAddRequest.setBookTitle("Effective Java");
        bookAddRequest.setAuthor("Joshua Bloch");

        Book expectedBook = new Book();
        expectedBook.setIsbn("978-7-111");
        expectedBook.setBookTitle("Effective Java"); // 注意字段名可能不一样
        expectedBook.setAuthor("Joshua Bloch");


        when(bookRepository.existsByIsbn(bookAddRequest.getIsbn())).thenReturn(false);
        when(bookMapper.toEntity(bookAddRequest)).thenReturn(expectedBook);

        bookService.addBook(bookAddRequest);

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(bookArgumentCaptor.capture());

        Book actualBook = bookArgumentCaptor.getValue();

        assertNotNull(actualBook);
        assertEquals(expectedBook, actualBook);

        assertAll("验证保存的书籍信息",
                () -> assertEquals("978-7-111", actualBook.getIsbn()),
                () -> assertEquals("Effective Java", actualBook.getBookTitle()),
                () -> assertEquals("Joshua Bloch", actualBook.getAuthor())
        );
    }

    @Test
    void searchBookByKeyword_keywordIsNull() {
        Book bookA = new Book();
        bookA.setIsbn("978-7-111");
        bookA.setBookTitle("Effective Java");
        bookA.setAuthor("Joshua Bloch");

        BookSearchResponse bookSearchResponseA = new BookSearchResponse();
        bookSearchResponseA.setIsbn("978-7-111");
        bookSearchResponseA.setBookTitle("Effective Java");
        bookSearchResponseA.setAuthor("Joshua Bloch");

        Book bookB = new Book();
        bookB.setIsbn("12345");
        bookB.setBookTitle("react");
        bookB.setAuthor("someAuthor");

        BookSearchResponse bookSearchResponseB = new BookSearchResponse();
        bookSearchResponseB.setIsbn("12345");
        bookSearchResponseB.setBookTitle("react");
        bookSearchResponseB.setAuthor("someAuthor");

        List<Book> bookList = new ArrayList<>();
        bookList.add(bookA);
        bookList.add(bookB);

        when(bookRepository.findAll()).thenReturn(bookList);
        when(bookMapper.toBookSearchResponse(bookA)).thenReturn(bookSearchResponseA);
        when(bookMapper.toBookSearchResponse(bookB)).thenReturn(bookSearchResponseB);

        List<BookSearchResponse> result = bookService.searchBooksByKeyword(null);

        assertAll("所有书本均正确返回",
                () -> assertTrue(result.contains(bookSearchResponseA)),
                () -> assertTrue(result.contains(bookSearchResponseB))
        );

    }

    @Test
    void searchBookByKeyword_keywordIsBlank() {
        Book bookA = new Book();
        bookA.setIsbn("978-7-111");
        bookA.setBookTitle("Effective Java");
        bookA.setAuthor("Joshua Bloch");

        BookSearchResponse bookSearchResponseA = new BookSearchResponse();
        bookSearchResponseA.setIsbn("978-7-111");
        bookSearchResponseA.setBookTitle("Effective Java");
        bookSearchResponseA.setAuthor("Joshua Bloch");

        Book bookB = new Book();
        bookB.setIsbn("12345");
        bookB.setBookTitle("react");
        bookB.setAuthor("someAuthor");

        BookSearchResponse bookSearchResponseB = new BookSearchResponse();
        bookSearchResponseB.setIsbn("12345");
        bookSearchResponseB.setBookTitle("react");
        bookSearchResponseB.setAuthor("someAuthor");

        List<Book> bookList = new ArrayList<>();
        bookList.add(bookA);
        bookList.add(bookB);

        when(bookRepository.findAll()).thenReturn(bookList);
        when(bookMapper.toBookSearchResponse(bookA)).thenReturn(bookSearchResponseA);
        when(bookMapper.toBookSearchResponse(bookB)).thenReturn(bookSearchResponseB);

        List<BookSearchResponse> result = bookService.searchBooksByKeyword("");

        assertAll("所有书本均正确返回",
                () -> assertTrue(result.contains(bookSearchResponseA)),
                () -> assertTrue(result.contains(bookSearchResponseB))
        );

    }

    @Test
    void searchBookByKeyword_keywordIsSomeString() {
        Book bookA = new Book();
        bookA.setIsbn("978-7-111");
        bookA.setBookTitle("Effective Java");
        bookA.setAuthor("Joshua Bloch");

        BookSearchResponse bookSearchResponseA = new BookSearchResponse();
        bookSearchResponseA.setIsbn("978-7-111");
        bookSearchResponseA.setBookTitle("Effective Java");
        bookSearchResponseA.setAuthor("Joshua Bloch");

        Book bookB = new Book();
        bookB.setIsbn("12345");
        bookB.setBookTitle("react");
        bookB.setAuthor("someAuthor");

        BookSearchResponse bookSearchResponseB = new BookSearchResponse();
        bookSearchResponseB.setIsbn("12345");
        bookSearchResponseB.setBookTitle("react");
        bookSearchResponseB.setAuthor("someAuthor");

        Book bookC = new Book();
        bookC.setIsbn("22345");
        bookC.setBookTitle("Anther react");
        bookC.setAuthor("someAuthor");

        BookSearchResponse bookSearchResponseC = new BookSearchResponse();
        bookSearchResponseC.setIsbn("22345");
        bookSearchResponseC.setBookTitle("Anther react");
        bookSearchResponseC.setAuthor("someAuthor");


        List<Book> expectedBookList = new ArrayList<>();
        expectedBookList.add(bookB);
        expectedBookList.add(bookC);

        String keyword = "react";

        when(bookRepository.findByBookTitleContainingOrAuthorContaining(keyword)).thenReturn(expectedBookList);

        when(bookMapper.toBookSearchResponse(bookB)).thenReturn(bookSearchResponseB);
        when(bookMapper.toBookSearchResponse(bookC)).thenReturn(bookSearchResponseC);

        List<BookSearchResponse> result = bookService.searchBooksByKeyword(keyword);

        verify(bookRepository, never()).findAll();
        verify(bookRepository).findByBookTitleContainingOrAuthorContaining(keyword);

        assertAll("所有书本均正确返回",
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(bookSearchResponseB)),
                () -> assertTrue(result.contains(bookSearchResponseC))
        );
    }


    @Test
    @DisplayName("测试场景：查询书籍失败-空指针")
    void searchBookByIsbn_FailBecauseNullPoint(){
        String isbn = null;
        assertNull(bookService.searchBookByIsbn(isbn));
    }

    @Test
    @DisplayName("测试场景：查询书籍失败-空字符串")
    void searchBookByIsbn_FailBecauseBlank(){
        String isbn = "";
        assertNull(bookService.searchBookByIsbn(isbn));
    }

    @Test
    @DisplayName("测试场景：查询书籍失败-找不到")
    void searchBookByIsbn_FailBecauseNotFound(){
        String isbn = "notFound";

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertNull(bookService.searchBookByIsbn(isbn));

        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("测试场景：查询书籍成功")
    void searchBookByIsbn_Success(){
        String isbn = "someIsbn";

        Book book = new Book();
        book.setId(1L);
        book.setIsbn(isbn);
        book.setBookTitle("someTitle");

        BookSearchResponse bookSearchResponse = new BookSearchResponse();
        bookSearchResponse.setId(1L);
        bookSearchResponse.setIsbn(isbn);
        bookSearchResponse.setBookTitle("someTitle");

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookMapper.toBookSearchResponse(book)).thenReturn(bookSearchResponse);

        BookSearchResponse result = bookService.searchBookByIsbn(isbn);

        assertNotNull(result);

        assertEquals(bookSearchResponse, result);

        verify(bookRepository).findByIsbn(isbn);
    }




}