package com.example.stemshop.web;

import com.example.stemshop.dto.ProductPageResponse;
import com.example.stemshop.dto.ProductResponse;
import com.example.stemshop.service.ProductService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ProductPageResponse<ProductResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @Min(0) Integer minPrice,
            @RequestParam(required = false) @Min(0) Integer maxPrice,
            @RequestParam(required = false) @Min(0) Integer page,
            @RequestParam(required = false) @Min(1) Integer size,
            @RequestParam(required = false) String sort // priceAsc|priceDesc|newest|rating
    ) {
        return ResponseEntity.ok(service.search(q, brandId, categoryId, minPrice, maxPrice, page, size, sort));
    }
}
