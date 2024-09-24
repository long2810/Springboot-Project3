package com.example.Project3.ShopMall.cart.repo;

import com.example.Project3.ShopMall.cart.entity.CartEntity;
import com.example.Project3.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
}
