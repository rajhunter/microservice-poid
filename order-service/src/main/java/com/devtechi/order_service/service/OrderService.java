package com.devtechi.order_service.service;

import com.devtechi.order_service.dto.InventoryResponse;
import com.devtechi.order_service.dto.OrderLineItemsDto;
import com.devtechi.order_service.dto.OrderRequest;
import com.devtechi.order_service.model.Order;
import com.devtechi.order_service.model.OrderLineItems;
import com.devtechi.order_service.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private  final  OrderRepository orderRepository;
private final WebClient.Builder webClientBuilder;

    private final ObservationRegistry observationRegistry;
private final Tracer tracer;
    public void  createOrder(OrderRequest orderRequest)  {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList =  orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDTO).toList();
        //  .map(orderLineItemsDto -> mapToDTO(orderLineItemsDto)).collect(Collectors.toList());
        order.setOrderLineItems(orderLineItemsList);
        orderRepository.save(order);

    }




    public String placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDTO)
                .toList();

        order.setOrderLineItems(orderLineItemsList);

        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        return Observation.createNotStarted("inventoryServiceCall", observationRegistry) // ✅ use injected registry
                .observe(() -> {
                    InventoryResponse[] inventoryResponseArray = webClientBuilder.build()
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/inventory/getInventoryOrderList")
                                    .queryParam("skuCode", skuCodes.toArray())
                                    .build())
                            .retrieve()
                            .bodyToMono(InventoryResponse[].class)
                            .block();

                    boolean allProductInStock = Arrays.stream(inventoryResponseArray)
                            .allMatch(InventoryResponse::isInStock);

                    if (allProductInStock) {
                        orderRepository.save(order);
                        return "Order placed successfully";
                    } else {
                        try {
                            throw new IllegalAccessException("Product is not available. Please try later!");
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }


    public String placeOrderOld(OrderRequest orderRequest) throws IllegalAccessException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList =  orderRequest.getOrderLineItemsDtoList()
                .stream()
                        .map(this::mapToDTO).toList();
              //  .map(orderLineItemsDto -> mapToDTO(orderLineItemsDto)).collect(Collectors.toList());
//http://localhost:8082/api/inventory/getAllInventory
        order.setOrderLineItems(orderLineItemsList);

       // order.getOrderLineItems().stream().map(orderLineItems -> orderLineItems.getSkuCode()).toList();
        List<String> skuCodes = order.getOrderLineItems()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        log.info("order service skuCodes list {}", skuCodes);

        /*InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory/getInventoryOrderList",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        */

     Span inventoryServiceSpan =  tracer.nextSpan().name("inventoryServiceSpan");
    try (Tracer.SpanInScope spanInScope=tracer.withSpan(inventoryServiceSpan.start())){


        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory/getInventoryOrderList", uriBuilder -> {
                    uriBuilder.queryParam("skuCode", skuCodes);
                    String finalUri = uriBuilder.build().toString();
                    log.info("Calling Inventory Service with URI: {}", finalUri);
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        log.info("order service inventoryResponseArray length: {}",
                inventoryResponseArray != null ? inventoryResponseArray.length : 0);
        assert inventoryResponseArray != null;
        for (InventoryResponse sku : inventoryResponseArray ) {
            System.out.println("order service inventoryResponseArray list {}"+sku.getSkuCode());

        }

        boolean allProductInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);
        if(allProductInStock){
            orderRepository.save(order);
            return " Order Place successfully";

        }else {
            throw new IllegalAccessException ("Product is not available Plea try latter !");
        }
    }finally {
        inventoryServiceSpan.end();
    }

    }

    private OrderLineItems mapToDTO(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setId(orderLineItemsDto.getId());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

return orderLineItems;

    }

    public List<Order> getAllProduct() {
        return orderRepository.findAll();



    }
    // Note this snippet is not working
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> new Order(order.getId(), order.getOrderNumber(),
                        order.getOrderLineItems().stream()
                                .map(li -> new OrderLineItems(li.getId(), li.getSkuCode(), li.getPrice(), li.getQuantity()))
                                .toList()))
                .toList();
    }

    public Optional<Order> getProductById(Long id) {
        return orderRepository.findById(id);
    }
}
