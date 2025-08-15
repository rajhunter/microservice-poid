package com.devtechi.order_service.service;

import com.devtechi.order_service.dto.InventoryResponse;
import com.devtechi.order_service.dto.OrderLineItemsDto;
import com.devtechi.order_service.dto.OrderRequest;
import com.devtechi.order_service.model.Order;
import com.devtechi.order_service.model.OrderLineItems;
import com.devtechi.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private  final  OrderRepository orderRepository;
private final WebClient webClient;

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

    public void  placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
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
        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory/getInventoryOrderList", uriBuilder -> {
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
        }else {
            throw new IllegalAccessException ("Product is not available Plea try latter !");
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
    public Optional<Order> getProductById(Long id) {
        return orderRepository.findById(id);
    }
}
