package com.devtechi.order_service.controller;

import com.devtechi.order_service.dto.OrderRequest;
import com.devtechi.order_service.model.Order;
import com.devtechi.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {


    private  final OrderService orderService;


    @PostMapping("/createOrder")
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequest orderRequest) {
        System.out.println("Incoming OrderRequest: " + orderRequest); // prints using toString()

        orderService.createOrder(orderRequest);
        return "Order created  successfully places ";
    }

    @PostMapping("/placeOrder")
    @ResponseStatus(HttpStatus.OK)
    public String placeOrder(@RequestBody OrderRequest orderRequest) throws IllegalAccessException {
        System.out.println("Incoming OrderRequest: " + orderRequest); // prints using toString()

        orderService.placeOrder(orderRequest);
        return "Order successfully places ";
    }

    @GetMapping("/getAllOrder")
    public List<Order> getAll(){
        return orderService.getAllProduct();
    }

    @GetMapping("/getProductById/{id}")
    public Optional<Order> getProductById(@PathVariable Long id){
        return orderService.getProductById(id);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return orderService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(
                        (Order) Map.of("message", "Product is out of stock, will notify once available. Thanks!")
                ));
    }

    @GetMapping("/getProductByExId/{id}")
    public ResponseEntity<?> getProductByIdEx(@PathVariable Long id) {
        return orderService.getProductById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(
                        Map.of("message", "Product is out of stock, will notify once available. Thanks!")
                ));
    }


}

