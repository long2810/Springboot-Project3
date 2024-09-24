package com.example.Project3.ShopMall.cart;

import com.example.Project3.ShopMall.product.entity.ProductEntity;
import com.example.Project3.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final UserRepository userRepository;
    private final CartService cartService;
//    @PostMapping("/addProduct")
//    public void addProduct(
//            @RequestBody
//            ProductEntity product
//    ) {
//        cartService.addProductToCart(product);
//    }


    @PostMapping("/delete/{cartId}")
    public void deleteCart(
            @PathVariable("cartId")
            Long cartId
    ) {
        cartService.deleteCart(cartId);
    }

    @PostMapping("/{cartId}/readProducts")
    public List<ProductEntity> readAll(
            @PathVariable("cartId")
            Long cartId
    ) {
        return cartService.readProductInCart(cartId);
    }
}
