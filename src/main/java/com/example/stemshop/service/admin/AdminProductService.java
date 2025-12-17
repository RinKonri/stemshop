package com.example.stemshop.service.admin;

import com.example.stemshop.domain.Product;
import com.example.stemshop.dto.admin.ProductUpsertRequest;
import com.example.stemshop.repo.ProductCategoryLinkRepository;
import com.example.stemshop.repo.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AdminProductService {
  private final ProductRepository products;
  private final ProductCategoryLinkRepository links;

  public AdminProductService(ProductRepository p, ProductCategoryLinkRepository l){
    this.products=p; this.links=l;
  }

  @Transactional
  public Long create(ProductUpsertRequest r) {
    Product p = Product.builder()
        .name(r.name())
        .article(r.article())
        .price(r.price())
        .photo(r.photo())
        .description(r.description())
        .technicalCharacteristics(r.technicalCharacteristics())
        .stock(r.stock())
        .brandId(r.brandId())
        .build();
    p = products.save(p);
    syncCategories(p.getId(), r.categoryIds());
    return p.getId();
  }

  @Transactional
  public void update(Long id, ProductUpsertRequest r) {
    Product p = products.findById(id).orElseThrow();
    p.setName(r.name());
    p.setArticle(r.article());
    p.setPrice(r.price());
    p.setPhoto(r.photo());
    p.setDescription(r.description());
    p.setTechnicalCharacteristics(r.technicalCharacteristics());
    p.setStock(r.stock());
    p.setBrandId(r.brandId());
    products.save(p);
    syncCategories(p.getId(), r.categoryIds());
  }

  @Transactional
  public void delete(Long id) {
    // сначала удалим связи
    links.deleteLinks(id);
    products.deleteById(id);
  }

  private void syncCategories(Long productId, java.util.Set<Long> cats){
    links.deleteLinks(productId);
    if (cats != null) for (Long cid : cats) links.addLink(productId, cid);
  }
}
