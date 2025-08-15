package com.devtechi.order_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="t_order")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    private String orderNumber;
    @OneToMany(cascade = CascadeType.ALL)
    List<OrderLineItems> orderLineItems;
}
