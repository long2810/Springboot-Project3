package com.example.Project3.ShopMall.product.repo;

import com.example.Project3.ShopMall.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByProductName(String name);
    Optional<ProductEntity> findByPrice(Double price);
    List<ProductEntity> findByPriceBetween(Double minPrice, Double maxPrice);
    List<ProductEntity> findByProductNameContaining(String productName);
}
