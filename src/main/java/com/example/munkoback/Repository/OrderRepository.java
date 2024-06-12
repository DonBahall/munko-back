package com.example.munkoback.Repository;

import com.example.munkoback.Model.Order.Order;

import com.example.munkoback.Model.Order.Status;
import com.example.munkoback.Model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findOrderByUserIdAndStatus(User userId, Status status);
}
