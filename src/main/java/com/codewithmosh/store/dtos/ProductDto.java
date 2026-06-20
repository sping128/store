package com.codewithmosh.store.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private BigDecimal price;
    private String description;
    private Byte categoryId;
}
