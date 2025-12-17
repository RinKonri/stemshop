package com.example.stemshop.service.admin;

import com.example.stemshop.domain.Category;
import com.example.stemshop.dto.admin.CategoryUpsertRequest;
import com.example.stemshop.repo.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AdminCategoryService {
    private final CategoryRepository categories;
    public AdminCategoryService(CategoryRepository c){ this.categories=c; }

    @Transactional public Long create(CategoryUpsertRequest r){
        Category c = Category.builder().name(r.name()).slug(r.slug()).parentId(r.parentId()).build();
        return categories.save(c).getId();
    }
    @Transactional public void update(Long id, CategoryUpsertRequest r){
        Category c = categories.findById(id).orElseThrow();
        c.setName(r.name()); c.setSlug(r.slug()); c.setParentId(r.parentId());
        categories.save(c);
    }
    @Transactional public void delete(Long id){ categories.deleteById(id); }
}
