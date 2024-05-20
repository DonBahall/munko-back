package com.example.munkoback.Resolver;

import com.example.munkoback.Model.Order.Order;
import com.example.munkoback.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;

import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class OrderMutationResolver {
    private final OrderService orderService;
    @MutationMapping
    public Order createOrder(@Argument String sessionID, @Argument Integer userId, @Argument Integer funkoId) {
        return orderService.saveOrder(sessionID, userId, funkoId);
    }
}
