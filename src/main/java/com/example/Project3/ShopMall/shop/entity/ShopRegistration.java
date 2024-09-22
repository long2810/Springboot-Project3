package com.example.Project3.ShopMall.shop.entity;

import com.example.Project3.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long register_id;

    private String shopName;
    private String description;

    @Enumerated(EnumType.STRING)
    private ShopState state;

    @Enumerated(EnumType.STRING)
    private ShopCategory category;

    @ManyToOne
    private UserEntity owner;

}