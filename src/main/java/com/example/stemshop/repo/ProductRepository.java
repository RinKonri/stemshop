package com.example.stemshop.repo;

import com.example.stemshop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<com.example.stemshop.domain.Product, Long> {

    @Query(value = "select name from products where id = :id", nativeQuery = true)
    String findNameById(Long id);

    @Query(value = "select price from products where id = :id", nativeQuery = true)
    Integer findPriceById(Long id);

    Integer getStockById(Long id);

    boolean existsById(Long id);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @Query(value = "select distinct pc.product_id from product_categories pc where pc.category_id in (:catIds)", nativeQuery = true)
    List<Long> findProductIdsByCategoryIds(@Param("catIds") List<Long> catIds);
}
