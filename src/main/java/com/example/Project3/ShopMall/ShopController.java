package com.example.Project3.ShopMall;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.ShopMall.shop.dto.ProductDto;
import com.example.Project3.ShopMall.shop.dto.ShopDto;
import com.example.Project3.ShopMall.shop.dto.ShopRegistrationDto;
import com.example.Project3.ShopMall.shop.entity.ProductEntity;
import com.example.Project3.ShopMall.shop.entity.ShopCategory;
import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import com.example.Project3.ShopMall.shop.entity.ShopRegistration;
import com.example.Project3.ShopMall.shop.repo.ShopAcceptWaitingRepository;
import com.example.Project3.ShopMall.shop.repo.ShopRepository;
import com.example.Project3.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shops")
public class ShopController {
    private final ShopService shopService;
    private final ShopRepository shopRepository;
    private final ShopAcceptWaitingRepository shopAcceptWaitingRepository;
    private final AuthenticationFacade facade;
    private final UserRepository userRepository;
    public ShopController(
            ShopService shopService,
            ShopRepository shopRepository,
            ShopAcceptWaitingRepository shopAcceptWaitingRepository,
            AuthenticationFacade facade,
            UserRepository userRepository
    ) {
        this.shopService = shopService;
        this.shopRepository = shopRepository;
        this.shopAcceptWaitingRepository = shopAcceptWaitingRepository;
        this.facade = facade;
        this.userRepository = userRepository;
    }


    ///shops/register
    @PostMapping("/register")
    public ShopRegistrationDto shopRegistration(
            @RequestParam("shopName")
            String shopName,
            @RequestParam("description")
            String description,
            @RequestParam("category")
            String category
    ) {
        ShopRegistrationDto shopRegistrationDto = new ShopRegistrationDto();
        shopRegistrationDto.setOwner(facade.getCurrentUserEntity(userRepository));
        shopRegistrationDto.setShopName(shopName);
        shopRegistrationDto.setDescription(description);
        shopRegistrationDto.setCategory(category);
        shopService.shopRegistration(shopRegistrationDto);
        return shopRegistrationDto;
    }

    // shops/shopList
    @GetMapping("/shopList")
    public List<ShopRegistration> shopRegistrationList() {
        return shopService.registrationList();
    }

    //shops/{shopId}/accept
    @GetMapping("/{shopId}/accept")
    public String acceptShop(
            @PathVariable("shopId")
            Long shopId
    ) {
        shopService.acceptShopByShopId(shopId);
        return "success";
    }

    // shops/{shopId}/decline
    @PostMapping("/{shopId}/decline")
    public String declineShop(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            String declineReason
    ) {
        shopService.declineShopByShopId(shopId,declineReason);
        return "decline";
    }

    // shops/{shopId}/close
    @PostMapping("/{shopId}/close")
    public String closeShop(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            String closeDemand
    ) {
        shopService.closeShopById(shopId,closeDemand);
        return "close success";
    }

    //shops/{shopId}/read
    @GetMapping("/{shopId}/read")
    public void readShop(
            @PathVariable("shopId")
            Long id
    ) {
        shopService.findShopById(id);
    }

    // shops/searchName
    @PostMapping("/searchName")
    public void searchByName(
            @RequestParam
            String shopName
    ) {
        shopService.searchShopByName(shopName);
    }

    @PostMapping("/searchByCategory")
    public void searchByCategory(
            @RequestParam
            String category
    ) {
        shopService.searchShopByCategory(category);
    }

    @GetMapping("/searchByDate")
    public List<ShopEntity> searchByDate() {
        return shopService.searchShopByPurchaseDate();
    }

    @PostMapping("{shopName}/products/register")
    public void productRegister(
            @PathVariable("shopName")
            String shopName,
            @RequestBody
            String product_name,
            @RequestBody
            String description,
            @RequestBody
            Double price,
            @RequestBody
            Long stock
    ) {
        ProductDto dto = new ProductDto();
        dto.setProduct_name(dto.getProduct_name());
        dto.setDescription(dto.getDescription());
        dto.setPrice(dto.getPrice());
        dto.setStock(dto.getStock());
        shopService.productRegistration(dto,shopName);
    }

    @PostMapping("{shopName}/products/{product_id}/update")
    public void updateProduct(
            @PathVariable("shopName")
            String shopName,
            @PathVariable("product_id")
            Long id,
            @RequestBody
            String product_name,
            @RequestBody
            String description,
            @RequestBody
            Double price,
            @RequestBody
            Long stock
    ) {
        ProductDto dto = new ProductDto();
        dto.setProduct_name(product_name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStock(stock);
        shopService.updateProduct(dto);
    }
}
