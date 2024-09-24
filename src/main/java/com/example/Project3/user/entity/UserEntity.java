package com.example.Project3.user.entity;

import com.example.Project3.ShopMall.cart.entity.CartEntity;
import com.example.Project3.ShopMall.shop.entity.ClosedShop;
import com.example.Project3.ShopMall.shop.entity.DeclinedShop;
import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import com.example.Project3.user.UserDetails.CustomUserDetails;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    private String username;

    private String password;

    private String nickname;

    private int age;

    private String email;

    private String phone;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserBusinessRegistration> businessList;

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL)
    private List<ShopEntity> shopList = new ArrayList<>();

//    @OneToMany(mappedBy = "declineUser")
//    List<DeclinedShop> declineShopList;
//
//    @OneToMany(mappedBy = "closeUser")
//    List<ClosedShop> closedShopList;
//
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY, optional = false)
//    private CartEntity user_cart;

    private double budget;

}
