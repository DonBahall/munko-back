package com.example.munkoback.Service;

import com.example.munkoback.Model.Order.Order;
import com.example.munkoback.Model.Order.OrderItem;
import com.example.munkoback.Model.Order.Status;
import com.example.munkoback.Repository.OrderRepository;
import com.example.munkoback.Request.OrderInfoRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final UserService userService;
    private final FunkoPopService funkoPopService;

    public Order saveOrder(String sessionID, Integer userId, OrderInfoRequest entity) {
        Order order;
        if (sessionID == null) {
            order = repository.findOrderByUserId(userService.findById(userId));
        } else if (userId == null) {
            order = repository.findOrderBySessionID(sessionID);
        } else {
            return null;
        }

        if (order == null) {
            order = new Order();
            if (sessionID == null) {
                order.setUserId(userService.findById(userId));
            } else {
                order.setSessionID(sessionID);
            }
            order.setOrderItems(new ArrayList<>());
            order.setStatus(Status.PENDING);
        }

        OrderItem item = new OrderItem(order, entity.funkoId, entity.amount, funkoPopService.getItem(entity.funkoId).getPrice());
        order.getOrderItems().add(item);
        repository.save(order);
        return order;
    }
}
