package com.devtechi.order_service.dto;

import com.devtechi.order_service.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
private List<OrderLineItemsDto> orderLineItemsDtoList;

}
