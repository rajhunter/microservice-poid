package com.devtechi.order_service.controller;

import com.devtechi.order_service.dto.OrderRequest;
import com.devtechi.order_service.model.Order;
import com.devtechi.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    @CircuitBreaker(name = "inventory",fallbackMethod = "fallbackMethod")
    public String placeOrder(@RequestBody OrderRequest orderRequest) throws IllegalAccessException {
        System.out.println("Incoming OrderRequest: " + orderRequest); // prints using toString()

        orderService.placeOrder(orderRequest);
        return "Order successfully places ";
    }

    @GetMapping("/getAllOrder")
    @CircuitBreaker(name = "inventory",fallbackMethod = "fallbackMethodGetAll")
    public List<Order> getAll(){
        return orderService.getAllProduct();
    }

    @GetMapping("/getProductById/{id}")
    public Optional<Order> getProductById(@PathVariable Long id){
        return orderService.getProductById(id);
    }
    @GetMapping("/products/{id}")
    @CircuitBreaker(name = "inventory",fallbackMethod = "fallbackMethodGetProductById")
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



    public  String fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException){
        return "OOps something went wrong please try after some time!";

    }

    public List<Order> fallbackMethodGetAll(RuntimeException ex) {
        // return an empty list or default response
        Order fallbackOrder = new Order();
        fallbackOrder.setId(-1L); // special id
        fallbackOrder.setOrderNumber("⚠️ Service unavailable. Please try again later.");
        return List.of(fallbackOrder);
    }

    public ResponseEntity<?> fallbackMethodGetProductById(Long id, Throwable ex) {
        return ResponseEntity.ok(
                Map.of(
                        "message", "⚠️ Inventory service is unavailable, please try again later.",
                        "productId", id,
                        "error", ex.getMessage()
                )
        );
    }
}

