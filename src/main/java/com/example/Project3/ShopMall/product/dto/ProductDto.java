package com.example.Project3.ShopMall.product.dto;

import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String productName;
    private String product_image_path;
    private String description;
    private Double price;
    private Long stock;
    ShopEntity product_shop;
}
