package com.example.Project3.user.entity;

import com.example.Project3.ShopMall.shop.entity.Cart;
import com.example.Project3.ShopMall.shop.entity.ClosedShop;
import com.example.Project3.ShopMall.shop.entity.DeclinedShop;
import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_table")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nickname;

    private int age;

    private String email;

    private String phone;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private List<UserBusinessRegistration> businessList;

    @OneToMany(mappedBy = "owner")
    private List<ShopEntity> shopList;

    @OneToMany(mappedBy = "declineUser")
    List<DeclinedShop> declineShopList;

    @OneToMany(mappedBy = "closeUser")
    List<ClosedShop> closedShopList;

    @OneToOne
    Cart user_cart;

    private double budget;

}
