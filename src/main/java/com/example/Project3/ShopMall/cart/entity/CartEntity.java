package com.example.Project3.ShopMall.cart.entity;

import com.example.Project3.ShopMall.product.entity.ProductEntity;
import com.example.Project3.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "cart_table")
@NoArgsConstructor
public class CartEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<ProductEntity> orders = new ArrayList<>();

//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id",referencedColumnName = "id",unique = true)
//    private UserEntity user;

//    public static CartEntity createCart(UserEntity user){
//        CartEntity  cart = new CartEntity();
//        cart.setUser(user);
//        return cart;
//    }
}
