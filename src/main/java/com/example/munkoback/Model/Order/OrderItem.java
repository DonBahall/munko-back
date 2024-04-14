package com.example.munkoback.Model.Order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    public OrderItem(Order order, Integer funkoId, Integer amount, Integer pricePerItem) {
        this.order = order;
        this.funkoId = funkoId;
        this.amount = amount;
        this.pricePerItem = pricePerItem;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer funkoId;
    private Integer amount;
    private Integer pricePerItem;

}
