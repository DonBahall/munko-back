package com.example.munkoback.Repository;

import com.example.munkoback.Model.Order.Order;
import com.example.munkoback.Model.Order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    Optional<OrderItem> findByOrderAndId(Order order, Integer id);
}
