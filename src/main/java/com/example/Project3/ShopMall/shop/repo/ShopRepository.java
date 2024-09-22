package com.example.Project3.ShopMall.shop.repo;

import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import com.example.Project3.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity,Long> {
    Optional<ShopEntity> findByShopName(String ShopName);
    boolean existsByShopName(String ShopName);
}
