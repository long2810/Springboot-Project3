package com.example.Project3.ShopMall.shop.dto;

import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import com.example.Project3.ShopMall.shop.entity.ShopState;
import com.example.Project3.user.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ShopDto {
    private String name;
    private String description;
    private ShopState state;
    private String category;
    private UserEntity owner;
    private String declineReason;

    ShopDto fromEntity(ShopEntity entity) {
        return ShopDto.builder()
                .name(entity.getShopName())
                .description(entity.getDescription())
                .state(entity.getState())
                .category(entity.getCategory().name())
                .owner(entity.getOwner())
                .build();
    }
}
