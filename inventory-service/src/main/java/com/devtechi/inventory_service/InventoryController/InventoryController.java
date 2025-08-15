package com.devtechi.inventory_service.InventoryController;

import com.devtechi.inventory_service.dto.InventoryResponse;
import com.devtechi.inventory_service.inventoryService.InventoryService;
import com.devtechi.inventory_service.model.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/getStockStatus/{sku-code}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku-code") String skuCode){

        return inventoryService.inStock(skuCode);
    }

    @GetMapping("/getInventoryStatus/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStockList(@PathVariable List<String> skuCode) {
        System.out.println("isInStockList{}"+InventoryController.class);
        return inventoryService.inStockInList(skuCode);
    }




    @GetMapping("/getInventoryOrderList")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        System.out.println("Received SKU codes: " + skuCode);
        return inventoryService.inStockInList(skuCode);
    }


    @GetMapping("/getAllInventory")
    @ResponseStatus(HttpStatus.OK)
    public List<Inventory> getAllInventory(){

        return inventoryService.findAllInventory();
    }

}
