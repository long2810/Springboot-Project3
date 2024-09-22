package com.example.Project3.ShopMall.shop.entity;

import com.example.Project3.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order_cart")
    List<Order> orders = new ArrayList<>();

    @OneToOne
    UserEntity owner;
}
