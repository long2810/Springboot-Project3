package com.example.Project3.ShopMall.shop.dto;

import com.example.Project3.ShopMall.shop.entity.ShopCategory;
import com.example.Project3.ShopMall.shop.entity.ShopRegistration;
import com.example.Project3.ShopMall.shop.entity.ShopState;
import com.example.Project3.user.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopRegistrationDto {
    private String shopName;
    private String description;
    private String category;
    private UserEntity owner;

    ShopRegistrationDto fromEntity(ShopRegistration entity) {
        return ShopRegistrationDto.builder()
                .shopName(entity.getShopName())
                .description(entity.getDescription())
                .category(entity.getCategory().name())
                .owner(entity.getOwner())
                .build();
    }
}
