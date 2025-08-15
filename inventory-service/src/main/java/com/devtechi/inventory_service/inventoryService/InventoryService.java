package com.devtechi.inventory_service.inventoryService;

import com.devtechi.inventory_service.dto.InventoryResponse;
import com.devtechi.inventory_service.model.Inventory;
import com.devtechi.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
   @Transactional(readOnly = true)
    public boolean  inStock(String skuCode){
        return  inventoryRepository.findBySkuCode(skuCode).isPresent();
    }

    public List<Inventory> findAllInventory() {
       return inventoryRepository.findAll();
    }

        public List<InventoryResponse> inStockInList(List<String> skuCode) {
            return inventoryRepository.findBySkuCodeIn(skuCode)
                    .stream()
                    .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity()>0)
                                .build()).toList();
        }



}
