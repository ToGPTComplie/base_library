package com.example.library.mapper;

import com.example.library.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toEntity(BookAddRequest request);

    BookSearchResponse toBookSearchResponse(Book book);
}
