package com.example.Project3.ShopMall.shop.dto;

import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String product_name;
    private MultipartFile product_image;
    private String description;
    private Double price;
    private Long stock;
    ShopEntity product_shop;
}
