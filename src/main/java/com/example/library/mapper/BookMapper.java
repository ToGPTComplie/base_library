package com.example.library.mapper;

import com.example.library.dto.BookAddRequest;
import com.example.library.dto.BookSearchResponse;
import com.example.library.entity.Book;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toEntity(BookAddRequest request);

    BookSearchResponse toBookSearchResponse(Book book);
}
