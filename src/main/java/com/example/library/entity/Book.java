package com.example.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(name = "uk_isbn", columnNames = "isbn")
})
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "ISBN 不能为空")
    @Column(name = "isbn", unique = true, nullable = false, length = 20)
    private String isbn;

    @NotBlank(message = "书名不能为空")
    @Column(name = "book_title", nullable = false, length = 100)
    private String bookTitle;

    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者姓名不能超过50个字符")
    @Column(name = "author", nullable = false, length = 50)
    private String author;

    @Size(max = 1023, message = "描述不能超过1023字符")
    @Column(name = "description", length = 1023)
    private String description;

    @Size(max = 50, message = "分类不能超过50个字符")
    @Column(name = "category", length = 50)
    private String category;

    @Min(value = 0, message = "总库存不能小于0")
    @Column(name = "total_stock", nullable = false)
    private Integer totalStock;

    @Min(value = 0, message = "可用库存不能小于0")
    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;
}
