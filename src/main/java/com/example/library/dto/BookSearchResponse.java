package com.example.library.dto;

import com.example.library.entity.Book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchResponse {
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String isbn;

    @NotBlank
    @Size(max = 100)
    private String bookTitle;

    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者姓名不能超过50个字符")
    private String author;

    @Size(max = 50, message = "分类不能超过50个字符")
    private String category;

    @Min(value = 0, message = "可用库存不能小于0")

    private Integer availableStock;
}
