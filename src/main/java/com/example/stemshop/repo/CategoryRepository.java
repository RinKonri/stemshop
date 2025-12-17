package com.example.stemshop.repo;

import com.example.stemshop.domain.Category;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // все id в поддереве (включая саму категорию)
    @Query(value = """
    with recursive cte as (
      select id, parent_id from categories where id = :rootId
      union all
      select c.id, c.parent_id from categories c
      join cte on c.parent_id = cte.id
    )
    select id from cte
  """, nativeQuery = true)
    List<Long> findSubtreeIds(@Param("rootId") Long rootId);
}
