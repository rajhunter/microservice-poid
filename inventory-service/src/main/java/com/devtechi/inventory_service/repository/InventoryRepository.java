package com.devtechi.inventory_service.repository;

import com.devtechi.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuCode(String skuCode); // parameter added

    List<Inventory> findBySkuCodeIn(List<String> skuCodes);

}

