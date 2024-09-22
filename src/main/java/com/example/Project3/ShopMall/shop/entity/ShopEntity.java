package com.example.Project3.ShopMall.shop.entity;

import com.example.Project3.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shop_table")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long shop_id;

    String shopName;
    String description;

    @Enumerated(EnumType.STRING)
    private ShopState state;

    @Enumerated(EnumType.STRING)
    private ShopCategory category;

    LocalDate purchaseDate;

    @ManyToOne
    UserEntity owner;

    @OneToMany(mappedBy = "product_shop")
    List<ProductEntity> products;

}
