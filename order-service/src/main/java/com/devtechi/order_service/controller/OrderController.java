package com.devtechi.order_service.controller;

import com.devtechi.order_service.dto.OrderRequest;
import com.devtechi.order_service.model.Order;
import com.devtechi.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    @TimeLimiter(name="inventory")
    @Retry(name="inventory")
    public CompletableFuture <String> placeOrder(@RequestBody OrderRequest orderRequest) throws Exception {
        System.out.println("Incoming OrderRequest: " + orderRequest); // prints using toString()
        return CompletableFuture.supplyAsync(()-> {
            try {
                return orderService.placeOrder(orderRequest);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        //return "Order successfully places ";
    }

    @GetMapping("/getAllOrder")
    public List<Order> getAllOrder(){
        return orderService.getAllProduct();
    }

//    @GetMapping("/getAllOrderAsync")
//    @TimeLimiter(name="inventory")
//    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethodGetAllAsync")
//    public CompletableFuture<List<Order>> getAll() {
//        return CompletableFuture.supplyAsync(() -> {
//            // explicitly force initialization while session is open
//            List<Order> orders = orderService.getAllProduct();
//            orders.forEach(order -> {
//                System.out.println("Order: " + order.getId() + " items = " + order.getOrderLineItems().size());
//            });
//            orders.forEach(o -> o.getOrderLineItems().size()); // triggers lazy load
//            return orders;
//        });
//    }


    // Note this snippet is not working
    @TimeLimiter(name="inventory")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethodGetAllAsync")
    @GetMapping("/getAllOrderAsync")
    public CompletableFuture<List<Order>> getAllOrderAsyc() {
        return CompletableFuture.supplyAsync(() -> orderService.getAllOrders());
    }

    public CompletableFuture<List<Order>> fallbackMethodGetAllAsync(Throwable ex) {
        Order fallbackOrder = new Order();
        fallbackOrder.setId(-1L);
        fallbackOrder.setOrderNumber("⚠️ Service unavailable. Please try again later.");

        return CompletableFuture.completedFuture(List.of(fallbackOrder));
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

