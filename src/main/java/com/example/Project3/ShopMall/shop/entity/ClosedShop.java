package com.example.Project3.ShopMall.shop.entity;

import com.example.Project3.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ClosedShop {
    private Long closeShop_id;
    private String closeDemand;
    private UserEntity closedUser;
}
