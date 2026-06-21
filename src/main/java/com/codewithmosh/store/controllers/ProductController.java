package com.codewithmosh.store.controllers;

import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Category;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    @PageableAsQueryParam
    public Page<ProductDto> getAllProducts(
            @RequestParam(required = false, name = "categoryId") Long categoryId,
            @Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageQuery) {
        Page<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId, pageQuery);
        } else {
            products = productRepository.findAll(pageQuery);
        }
        return products.map(productMapper::toDto);
    }

    @PostMapping
    public ResponseEntity<ProductDto> CreateProduct(
            @RequestBody ProductDto dto,
            UriComponentsBuilder uriBuilder) {
        Product product = productMapper.toEntity(dto);
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElse(null);
            if (category == null)
                return ResponseEntity.badRequest().build();
            product.setCategory(category);
        }
        productRepository.save(product);
        dto.setId(product.getId());
        var uri = uriBuilder.path("/api/products/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductDto> EditProduct(@PathVariable Long id, @RequestBody ProductDto dto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        productMapper.update(dto, product);
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElse(null);
            if (category == null)
                return ResponseEntity.badRequest().build();
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        productRepository.save(product);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> DeleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
