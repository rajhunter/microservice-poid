package com.devtechi.inventory_service.inventoryService;

import com.devtechi.inventory_service.dto.InventoryResponse;
import com.devtechi.inventory_service.model.Inventory;
import com.devtechi.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
   @Transactional(readOnly = true)
    public boolean  inStock(String skuCode){

       return  inventoryRepository.findBySkuCode(skuCode).isPresent();
    }

    public List<Inventory> findAllInventory() {
       return inventoryRepository.findAll();
    }

    @SneakyThrows // it is not recommended in Production
    public List<InventoryResponse> inStockInList(List<String> skuCode) {
       log.info("Inventory Start");
       Thread.sleep(10000);
        log.info("Inventory End");

        return inventoryRepository.findBySkuCodeIn(skuCode)
                    .stream()
                    .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity()>0)
                                .build()).toList();
        }



}
