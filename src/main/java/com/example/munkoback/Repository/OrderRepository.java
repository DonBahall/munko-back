package com.example.munkoback.Repository;

import com.example.munkoback.Model.Order.Order;

import com.example.munkoback.Model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderByUserId(User userId);
    Order findOrderBySessionID(String sessionID);
}
