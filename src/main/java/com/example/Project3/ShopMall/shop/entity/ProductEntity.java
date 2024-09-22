package com.example.Project3.ShopMall.shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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
    private String product_name;
    private MultipartFile product_image;
    private String description;
    private Double price;
    private Long stock;

    @ManyToOne
    ShopEntity product_shop;

    @ManyToOne
    Order order;

    private int purchase_count;
}
