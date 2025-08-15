package com.devtechi.product_service.controller;

import com.devtechi.product_service.dto.ProductRequest;
import com.devtechi.product_service.dto.ProductResponse;
import com.devtechi.product_service.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;




    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest){
productService.createProduct(productRequest);

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ProductResponse> getAllProductList(){
return productService.getAllProduct();
    }

}
