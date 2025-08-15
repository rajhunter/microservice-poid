package com.devtechi.product_service.service;

import com.devtechi.product_service.dto.ProductRequest;
import com.devtechi.product_service.dto.ProductResponse;
import com.devtechi.product_service.model.Product;
import com.devtechi.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // its coming from lombok
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product{} is saved",product.getId()); // {} is place holder
    }


    public List<ProductResponse> getAllProduct() {
      List<Product>  products =productRepository.findAll();
  //  return   products.stream().map(product -> mapToProductResponse(product)).toList();
    //return products.stream().map(this::mapToProductResponse).toList();
       // return   products.stream().map(this::mapToProductResponse).toList();
        return products.stream().map(this::mapToProductResponse).collect(Collectors.toList());


    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
