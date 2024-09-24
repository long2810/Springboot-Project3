package com.example.Project3.ShopMall.product;

import com.example.Project3.ShopMall.product.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(
            ProductService productService
    ) {
        this.productService = productService;
    }
    @PostMapping("/register")
    public void productRegister(
            @RequestBody
            String shopName,
            @RequestBody
            String productName,
            @RequestBody
            String description,
            @RequestBody
            Double price,
            @RequestBody
            Long stock
    ) {
        ProductDto dto = new ProductDto();
        dto.setProductName(productName);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStock(stock);
        productService.productRegistration(dto,shopName);
    }

    @PostMapping("/{product_id}/update")
    public void updateProduct(
            @PathVariable("product_id")
            Long id,
            @RequestBody
            String shopName,
            @RequestBody
            String productName,
            @RequestBody
            String description,
            @RequestBody
            Double price,
            @RequestBody
            Long stock
    ) {
        ProductDto dto = new ProductDto();
        dto.setProductName(productName);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setStock(stock);
        productService.updateProduct(dto);
    }

    @PostMapping("/{product_id}/delete")
    public void deleteProduct(
            @PathVariable("product_id")
            Long product_id
    ) {
        productService.deleteProduct(product_id);
    }

}
