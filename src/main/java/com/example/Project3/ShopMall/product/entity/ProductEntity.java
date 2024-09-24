package com.example.Project3.ShopMall.product.entity;

import com.example.Project3.ShopMall.cart.entity.CartEntity;
import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_table")
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String product_image_path;
    private String description;
    private Double price;
    private Long stock;

    @ManyToOne
    ShopEntity product_shop;

    @ManyToOne
    CartEntity cart;
    private int purchase_count;
}
