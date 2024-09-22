package com.example.Project3.ShopMall;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.ShopMall.shop.dto.ProductDto;
import com.example.Project3.ShopMall.shop.dto.ShopRegistrationDto;
import com.example.Project3.ShopMall.shop.entity.*;
import com.example.Project3.ShopMall.shop.repo.ProductRepository;
import com.example.Project3.ShopMall.shop.repo.ShopAcceptWaitingRepository;
import com.example.Project3.ShopMall.shop.repo.ShopRepository;
import com.example.Project3.user.entity.UserEntity;
import com.example.Project3.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShopService {
    private final ShopRepository shopRepository;
    private final ShopAcceptWaitingRepository shopAcceptWaitingRepository;
    private final AuthenticationFacade facade;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    public ShopService(
            ShopRepository shopRepository,
            ShopAcceptWaitingRepository shopAcceptWaitingRepository,
            AuthenticationFacade facade,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.shopRepository = shopRepository;
        this.shopAcceptWaitingRepository = shopAcceptWaitingRepository;
        this.userRepository = userRepository;
        this.facade = facade;
        this.productRepository = productRepository;
        ShopEntity shop1 = new ShopEntity();
        shop1.setShopName("shop1");
        shop1.setDescription("This is Shop1");
        shop1.setCategory(ShopCategory.BEAUTY);
        shopRepository.save(shop1);
        log.info(String.format("shop1: %s",shop1.toString()));
    }

    public void shopRegistration (
            ShopRegistrationDto shopRegistrationDto
    ) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("BUSINESS"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not Business");
        ShopRegistration newShop = new ShopRegistration();
        newShop.setOwner(currentUser);
        newShop.setShopName(shopRegistrationDto.getShopName());
        newShop.setDescription(shopRegistrationDto.getDescription());
        newShop.setCategory(ShopCategory.valueOf(shopRegistrationDto.getCategory()));
        newShop.setState(ShopState.PREPARED);
        shopAcceptWaitingRepository.save(newShop);
    }

    public List<ShopRegistration> registrationList() {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN so can't check the registrationList");

        List<ShopRegistration> acceptWaitingList = shopAcceptWaitingRepository.findAll();
        for (ShopRegistration entity : acceptWaitingList) {
            log.info(entity.getShopName().toString());
        }
        return acceptWaitingList;
    }

    public String acceptShopByShopId(Long shopId) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN so can't check the registrationList");

        Optional<ShopRegistration> optionalShop = shopAcceptWaitingRepository.findById(shopId);
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found a shop with this shopId");
        // Shop registration
        ShopRegistration acceptShop = optionalShop.get();

        // Convert all shopRegistration data to ShopEntity
        ShopEntity newShop = new ShopEntity();
        newShop.setDescription(acceptShop.getDescription());
        newShop.setState(ShopState.OPENED);
        newShop.setCategory(acceptShop.getCategory());
        newShop.setOwner(acceptShop.getOwner());
        newShop.setPurchaseDate(LocalDate.now());

        //delete shop accept waiting in accept waiting repo
        shopAcceptWaitingRepository.deleteById(shopId);

        //save new shop to shop repo
        shopRepository.save(newShop);
        return "success";
    }

    public String declineShopByShopId(Long shopId, String declineReason) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN so can't check the registrationList");

        Optional<ShopRegistration> optionalShop = shopAcceptWaitingRepository.findById(shopId);
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found a shop with this shopId in shop accept waiting repo");
        // Shop registration
        ShopRegistration declineShop = optionalShop.get();
        DeclinedShop shopDeclineEntity = new DeclinedShop();
        shopDeclineEntity.setDeclineReason(declineReason);
        currentUser.getDeclineShopList().add(shopDeclineEntity);
        shopAcceptWaitingRepository.deleteById(shopId);
        return "decline success";
    }

    public String closeShopById(Long shopId, String closeDemand) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN so can't check the registrationList");

        Optional<ShopEntity> optionalShop = shopRepository.findById(shopId);
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found a shop with this shopId in shop repository");

        ShopEntity deletedShop = optionalShop.get();
        ClosedShop closedShop = new ClosedShop();
        closedShop.setCloseDemand(closeDemand);
        currentUser.getClosedShopList().add(closedShop);
        shopRepository.deleteById(shopId);
        return "close shop Success";
    }
    public ShopEntity findShopById(Long id) {
        Optional<ShopEntity> optionalShop = shopRepository.findById(id);
        if(optionalShop.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalShop.get();
    }
    public List<ShopEntity> searchShopList() {
        return shopRepository.findAll();
    }

    public ShopEntity searchShopByName(String shopName) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (currentUser.getRole().name().equals("INACTIVE"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"user is inactive user so can not search the shop, please register account");
        Optional<ShopEntity> optionalShop = shopRepository.findByShopName(shopName);
        if(optionalShop.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalShop.get();
    }

    public List<ShopEntity> searchShopByCategory(String category) {
        List<ShopEntity> shopList = shopRepository.findAll();
        List<ShopEntity> shopListWithSameCategory = new ArrayList<>();

        for (ShopEntity entity : shopList) {
            if(entity.getCategory().equals(category)) {
                shopListWithSameCategory.add(entity);
            }
        }
        return shopListWithSameCategory;
    }

    public List<ShopEntity> searchShopByPurchaseDate() {
        List<ShopEntity> sortedShops = shopRepository.findAll() // Assuming you fetch all shops from repository
                .stream()
                .sorted(Comparator.comparing(ShopEntity :: getPurchaseDate))
                .collect(Collectors.toList());
        return sortedShops;
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
        product.setProduct_name(dto.getProduct_name());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setProduct_image(dto.getProduct_image());
        Optional<ShopEntity> optionalShop = shopRepository.findByShopName(shopName);
        product.setProduct_shop(optionalShop.get());
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

        Optional<ProductEntity> optionalProduct = productRepository.findByProductName(dto.getProduct_name());
        if(optionalProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ProductEntity product = optionalProduct.get();
        product.setProduct_name(dto.getProduct_name());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setProduct_image(dto.getProduct_image());
        log.info(product.getProduct_name());
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

    public Cart addOrderToCart(Order order) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("INACTIVE"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not USER so can't purchase product");
        Cart cart = currentUser.getUser_cart();
        cart.getOrders().add(order);
        return cart;
    }

    public double totalOrderPriceInCart() {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("INACTIVE"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not USER so can't purchase product");
        List<Order> orders = currentUser.getUser_cart().getOrders();
        if (orders.size() == 0 ) return 0;
        double totalPrice = 0;
        for (Order order : orders) {
            List<ProductEntity> productList = order.getProducts();
            for (ProductEntity product : productList) {
                double order_price = product.getPrice() * product.getPurchase_count();
                totalPrice += order_price;
            }
        }
        return totalPrice;
    }
}
