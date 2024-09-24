package com.example.Project3.ShopMall;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.ShopMall.product.dto.ProductDto;
import com.example.Project3.ShopMall.shop.dto.ShopDto;
import com.example.Project3.ShopMall.shop.dto.ShopRegistrationDto;
import com.example.Project3.ShopMall.shop.entity.ClosedShop;
import com.example.Project3.ShopMall.shop.entity.ShopEntity;
import com.example.Project3.ShopMall.shop.entity.ShopRegistration;
import com.example.Project3.ShopMall.shop.repo.ShopAcceptWaitingRepository;
import com.example.Project3.ShopMall.shop.repo.ShopRepository;
import com.example.Project3.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping("/shopWaitingList")
    public List<ShopRegistration> shopRegistrationList() {
        return shopService.registrationList();
    }

    @GetMapping("/shopList")
    public List<ShopDto> shopList() {
        return shopService.shopList();
    }

    //shops/{shopId}/accept
    @GetMapping("/{shopId}/accept")
    public String acceptShop(
            @PathVariable("shopId")
            Long shopId
    ) {
        shopService.acceptShopByShopId(shopId);
        return String.format("registration of shopId: %d success",shopId);
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
        return declineReason;
    }

    // shops/{shopId}/close
    @PostMapping("/{shopId}/close")
    public ClosedShop closeShop(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            String closeDemand
    ) {
        return shopService.closeShopById(shopId,closeDemand);
    }

    //shops/{shopId}/read
    @GetMapping("/{shopId}/read")
    public ShopDto readShop(
            @PathVariable("shopId")
            Long id
    ) {
        return shopService.findShopById(id);
    }

    // shops/searchName
    @PostMapping("/searchName")
    public List<ShopDto> searchByName(
            @RequestParam
            String shopName
    ) {
        return shopService.searchShopByName(shopName);
    }

    @PostMapping("/searchByCategory")
    public List<ShopDto> searchByCategory(
            @RequestParam
            String category
    ) {
       return shopService.searchShopByCategory(category);
    }

    @GetMapping("/searchByDate")
    public List<ShopEntity> searchByDate() {
        return shopService.searchShopByPurchaseDate();
    }
}
