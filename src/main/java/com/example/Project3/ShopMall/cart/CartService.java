package com.example.Project3.ShopMall.cart;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.ShopMall.cart.dto.CartDto;
import com.example.Project3.ShopMall.cart.entity.CartEntity;
import com.example.Project3.ShopMall.cart.repo.CartRepository;
import com.example.Project3.ShopMall.product.entity.ProductEntity;
import com.example.Project3.ShopMall.product.repo.ProductRepository;
import com.example.Project3.user.entity.UserEntity;
import com.example.Project3.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final AuthenticationFacade facade;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public static CartEntity createCart(UserEntity user) {
        CartEntity cart = new CartEntity();
        //cart.setCartOwner(user);
        return cart;
    }
//    public void addProductToCart(ProductEntity product) {
//        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
//        if (currentUser.getRole().name().equals("INACTIVE"))
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is INACTIVE so can't add product to cart");
//
//       CartEntity cart = currentUser.getUser_cart();
//       if (cart == null) {
//           cartRepository.save(createCart(currentUser));
//       }
//       cart.getOrders().add(product);
//    }
//    public void deleteProductInCart(Long productId) {
//        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
//        if (currentUser.getRole().name().equals("INACTIVE"))
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        CartEntity cart = currentUser.getUser_cart();
//        if(cart == null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//    }
    public void deleteCart(Long cartId) {
        Optional<CartEntity> optionalCart = cartRepository.findById(cartId);
        if(optionalCart.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        cartRepository.delete(optionalCart.get());
    }

    public List<ProductEntity> readProductInCart(Long cartId) {
        Optional<CartEntity> optionalCart = cartRepository.findById(cartId);
        if(optionalCart.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CartEntity cart = optionalCart.get();
        if (cart.getOrders().isEmpty())
            return null;
        else
            return cart.getOrders();
    }

}
