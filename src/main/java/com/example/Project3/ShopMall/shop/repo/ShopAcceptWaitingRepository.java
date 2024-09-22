package com.example.Project3.ShopMall.shop.repo;

import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import com.example.Project3.ShopMall.shop.entity.ShopRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopAcceptWaitingRepository extends JpaRepository<ShopRegistration,Long> {
    Optional<ShopEntity> findShopByShopName(String shopName);
}
