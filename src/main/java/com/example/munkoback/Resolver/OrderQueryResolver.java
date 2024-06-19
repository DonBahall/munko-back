package com.example.munkoback.Resolver;

import com.example.munkoback.Model.Order.OrderItem;
import com.example.munkoback.Service.OrderService;
import com.example.munkoback.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderQueryResolver {
    private final OrderService orderService;
    private final UserService userService;

    @QueryMapping
    public List<OrderItem> getOrderItems() {
        return orderService.getOrderItems( userService.getAutentificatedUser().getId());
    }
}
