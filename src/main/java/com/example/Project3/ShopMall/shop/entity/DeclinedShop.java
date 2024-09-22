package com.example.Project3.ShopMall.shop.entity;

import com.example.Project3.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class DeclinedShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long declineShop_id;

    private String declineReason;

    @ManyToOne
    UserEntity declineUser;
}
