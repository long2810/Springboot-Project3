package com.example.Project3.ShopMall;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.ShopMall.cart.entity.CartEntity;
import com.example.Project3.ShopMall.product.entity.ProductEntity;
import com.example.Project3.ShopMall.shop.dto.ShopDto;
import com.example.Project3.ShopMall.shop.dto.ShopRegistrationDto;
import com.example.Project3.ShopMall.shop.entity.*;
import com.example.Project3.ShopMall.product.repo.ProductRepository;
import com.example.Project3.ShopMall.shop.repo.ShopAcceptWaitingRepository;
import com.example.Project3.ShopMall.shop.repo.ShopRepository;
import com.example.Project3.user.entity.UserEntity;
import com.example.Project3.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

        ShopEntity shop2 = new ShopEntity();
        shop2.setShopName("shop2");
        shop2.setDescription("This is Shop2");
        shop2.setCategory(ShopCategory.BEAUTY);
        shopRepository.save(shop2);
    }

    public void shopRegistration (
            ShopRegistrationDto shopRegistrationDto
    ) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("BUSINESS"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not Business");
        boolean existShop = shopRepository.existsByShopName(shopRegistrationDto.getShopName());
        if (existShop) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"shopName is existed");
        }
        ShopRegistration newShop = new ShopRegistration();
        newShop.setOwner(currentUser);
        newShop.setShopName(shopRegistrationDto.getShopName());
        newShop.setDescription(shopRegistrationDto.getDescription());
        newShop.setCategory(ShopCategory.valueOf(shopRegistrationDto.getCategory()));
        newShop.setState(ShopState.PREPARED);
        log.info(newShop.getShopName());
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
        newShop.setShopName(acceptShop.getShopName());
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
        shopAcceptWaitingRepository.deleteById(shopId);
        return "decline success";
    }

    public ClosedShop closeShopById(Long shopId, String closeDemand) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN so can't check the registrationList");

        Optional<ShopEntity> optionalShop = shopRepository.findById(shopId);
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found a shop with this shopId in shop repository");

        ShopEntity deletedShop = optionalShop.get();
        ClosedShop closedShop = new ClosedShop();
        closedShop.setCloseShop_id(shopId);
        closedShop.setCloseDemand(closeDemand);
        closedShop.setClosedUser(currentUser);

        shopRepository.deleteById(shopId);
        return closedShop;
    }
    public List<ShopDto> shopList() {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (!currentUser.getRole().name().equals("ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN so can't check the registrationList");

        List<ShopEntity> shopList = shopRepository.findAll();
        for (ShopEntity entity : shopList) {
            log.info(entity.getShopName().toString());
        }
        return shopList.stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ShopDto findShopById(Long id) {
        Optional<ShopEntity> optionalShop = shopRepository.findById(id);
        if(optionalShop.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ShopDto.fromEntity(optionalShop.get());
    }
    public List<ShopEntity> searchShopList() {
        return shopRepository.findAll();
    }

    public List<ShopDto> searchShopByName(String shopName) {
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
        if (currentUser.getRole().name().equals("INACTIVE"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"user is inactive user so can not search the shop, please register account");
        List<ShopEntity> shopList = shopRepository.findAll();
        if(shopList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<ShopEntity> shopListWithSameName = new ArrayList<>();
        for (ShopEntity entity : shopList) {
            if (entity.getShopName().contains(shopName))
                shopListWithSameName.add(entity);
        }
        return shopListWithSameName.stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShopDto> searchShopByCategory(String category) {
        List<ShopEntity> shopList = shopRepository.findAll();
        if(shopList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<ShopEntity> shopListWithSameCategory = new ArrayList<>();

        for (ShopEntity entity : shopList) {
            if(entity.getCategory().name().contains(category)) {
                shopListWithSameCategory.add(entity);
            }
        }
        return shopListWithSameCategory.stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShopEntity> searchShopByPurchaseDate() {
        List<ShopEntity> sortedShops = shopRepository.findAll() // Assuming you fetch all shops from repository
                .stream()
                .sorted(Comparator.comparing(ShopEntity :: getPurchaseDate))
                .collect(Collectors.toList());
        return sortedShops;
    }

//    public double totalOrderPriceInCart() {
//        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);
//        if (!currentUser.getRole().name().equals("INACTIVE"))
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not USER so can't purchase product");
//        List<Order> orders = currentUser.getUser_cart().getOrders();
//        if (orders.size() == 0 ) return 0;
//        double totalPrice = 0;
//        for (Order order : orders) {
//            List<ProductEntity> productList = order.getProducts();
//            for (ProductEntity product : productList) {
//                double order_price = product.getPrice() * product.getPurchase_count();
//                totalPrice += order_price;
//            }
//        }
//        return totalPrice;
//    }
}
