package com.example.Project3.ShopMall.product;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.ShopMall.product.dto.ProductDto;
import com.example.Project3.ShopMall.product.entity.ProductEntity;
import com.example.Project3.ShopMall.product.repo.ProductRepository;
import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import com.example.Project3.ShopMall.shop.repo.ShopAcceptWaitingRepository;
import com.example.Project3.ShopMall.shop.repo.ShopRepository;
import com.example.Project3.user.entity.UserEntity;
import com.example.Project3.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    private final ShopRepository shopRepository;
    private final AuthenticationFacade facade;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ProductService(
            ShopRepository shopRepository,
            AuthenticationFacade facade,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.facade = facade;
        this.productRepository = productRepository;
        ProductEntity product = new ProductEntity();
        product.setProductName("product1");
        product.setDescription("This is product1");
        product.setPrice(100000.0);
        product.setStock(10L);
    }

    public void productRegistration(ProductDto dto, String shopName) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("BUSINESS"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not BUSINESS so can't register product");
        // check user is owner of shop or not
        if (!dto.getProduct_shop().getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not shop owner");
        }
        ProductEntity product = new ProductEntity();
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setProduct_image_path(dto.getProduct_image_path());
//        Optional<ShopEntity> optionalShop = shopRepository.existsByShopName(shopName);
//        product.setProduct_shop(optionalShop.get());
        productRepository.save(product);
    }
    public ProductEntity findProductById(Long id) {
        return productRepository.findById(id).get();
    }
    public ProductEntity updateProduct(ProductDto dto) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);

        if (!dto.getProduct_shop().getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not shop owner");
        }

        Optional<ProductEntity> optionalProduct = productRepository.findByProductName(dto.getProductName());
        if(optionalProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ProductEntity product = optionalProduct.get();
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setProduct_image_path(dto.getProduct_image_path());
        log.info(product.getProductName());
        return productRepository.save(product);
    }

    public void deleteProduct(Long product_id) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);

        Optional<ProductEntity> optionalProduct = productRepository.findById(product_id);
        if(optionalProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ProductEntity product = optionalProduct.get();
        if (!product.getProduct_shop().getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not shop owner");
        }

        productRepository.delete(product);
    }

    public ResponseEntity<List<ProductEntity>> searchProductByName(String product_name) {
        List<ProductEntity> products = productRepository.findByProductNameContaining(product_name);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }

    public ResponseEntity<List<ProductEntity>> searchProductByPrice(Double minPrice, Double maxPrice) {
        List<ProductEntity> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return products.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(products);
    }
}
