package com.example.munkoback.Resolver;

import com.example.munkoback.Model.Order.OrderItem;
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
    public OrderItem addItemInBasket(@Argument Integer userId, @Argument Integer funkoId) {
        return orderService.addItemInBasket(userId, funkoId);
    }
    @MutationMapping
    public Boolean deleteItemInBasket(@Argument Integer userId, @Argument Integer itemId) {
        return orderService.deleteItemInBasket(userId, itemId);
    }
    @MutationMapping
    public OrderItem updateItemInBasket(@Argument Integer userId, @Argument Integer funkoId, @Argument Integer amount) {
        return orderService.updateItemInBasket(userId, funkoId, amount);
    }
}
