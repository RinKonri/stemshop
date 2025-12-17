package com.example.stemshop.service;

import com.example.stemshop.domain.Product;
import com.example.stemshop.dto.ProductPageResponse;
import com.example.stemshop.dto.ProductResponse;
import com.example.stemshop.repo.CategoryRepository;
import com.example.stemshop.repo.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository products;
    private final CategoryRepository categories;

    public ProductService(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    public ProductPageResponse<ProductResponse> search(
            String q,
            Long brandId,
            Long categoryId,
            Integer minPrice,
            Integer maxPrice,
            Integer page,
            Integer size,
            String sort // "priceAsc" | "priceDesc" | "newest" | "rating"
    ) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 100) ? 20 : size;

        Sort by = switch (sort == null ? "" : sort) {
            case "priceAsc"  -> Sort.by(Sort.Direction.ASC,  "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating"    -> Sort.by(Sort.Direction.DESC, "rating");
            default          -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(p, s, by);

        // заранее посчитаем productIds по (sub)категории, если передана
        List<Long> productIdsByCat;
        if (categoryId != null) {
            var catIds = categories.findSubtreeIds(categoryId);
            if (catIds == null || catIds.isEmpty()) {
                return new ProductPageResponse<>(List.of(), p, s, 0, 0);
            }
            productIdsByCat = products.findProductIdsByCategoryIds(catIds);
            if (productIdsByCat.isEmpty()) {
                return new ProductPageResponse<>(List.of(), p, s, 0, 0);
            }
        } else {
            productIdsByCat = null;
        }

        // соберём спецификацию
        Specification<Product> spec = (root, cq, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                ps.add(cb.or(
                                cb.like(cb.lower(root.get("name")), like))
                );
            }
            if (brandId != null)    ps.add(cb.equal(root.get("brandId"), brandId));
            if (minPrice != null)   ps.add(cb.ge(root.get("price"), minPrice));
            if (maxPrice != null)   ps.add(cb.le(root.get("price"), maxPrice));
            if (productIdsByCat != null) ps.add(root.get("id").in(productIdsByCat));

            return cb.and(ps.toArray(new Predicate[0]));
        };

        Page<Product> pageData = products.findAll(spec, pageable);

        var items = pageData.getContent().stream().map(pv -> new ProductResponse(
                pv.getId(),
                pv.getName(),
                pv.getArticle(),
                pv.getPrice(),
                pv.getStock(),
                pv.getBrandId(),
                pv.getRating(),
                pv.getRatingCount(),
                pv.getPhoto()
        )).toList();

        return new ProductPageResponse<>(
                items,
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages()
        );
    }
}
