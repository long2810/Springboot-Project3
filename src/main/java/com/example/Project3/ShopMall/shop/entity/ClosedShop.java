package com.example.Project3.ShopMall.shop.entity;

import com.example.Project3.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ClosedShop {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long closeShop_id;

    private String closeDemand;

    @ManyToOne
    UserEntity closeUser;
}
