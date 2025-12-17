// repo/ProductCategoryLinkRepository.java
package com.example.stemshop.repo;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductCategoryLinkRepository extends JpaRepository<com.example.stemshop.domain.Product, Long> {
    @Modifying @Query(value="delete from product_categories where product_id=:pid", nativeQuery=true)
    void deleteLinks(@Param("pid") Long productId);

    @Modifying @Query(value="insert into product_categories(product_id, category_id) values (:pid, :cid)", nativeQuery=true)
    void addLink(@Param("pid") Long productId, @Param("cid") Long categoryId);
}
