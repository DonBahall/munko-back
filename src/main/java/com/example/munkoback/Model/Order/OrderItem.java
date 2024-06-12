package com.example.munkoback.Model.Order;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    public OrderItem(Order order, FunkoPop funkoPop, Integer amount) {
        this.order = order;
        this.funkoPop = funkoPop;
        this.amount = amount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @OneToOne
    @JoinColumn(name = "funko_pop_id", nullable = false)
    private FunkoPop funkoPop;
    private Integer amount;

}
