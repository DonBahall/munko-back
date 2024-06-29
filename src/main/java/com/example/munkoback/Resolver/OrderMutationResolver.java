package com.example.munkoback.Resolver;

import com.example.munkoback.Model.Order.OrderItem;
import com.example.munkoback.Service.OrderService;
import com.example.munkoback.Service.PayPalService;
import com.example.munkoback.Service.UserService;
import com.paypal.orders.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;

import org.springframework.stereotype.Controller;

import java.io.IOException;


@Controller
@RequiredArgsConstructor
public class OrderMutationResolver {
    private final OrderService orderService;
    private final PayPalService payPalService;
    private final UserService userService;
    @MutationMapping
    public OrderItem addItemInBasket(@Argument Integer funkoId) {
        return orderService.addItemInBasket(userService.getAutentificatedUser().getId(), funkoId);
    }
    @MutationMapping
    public Boolean deleteItemInBasket(@Argument Integer itemId) {
        return orderService.deleteItemInBasket(userService.getAutentificatedUser().getId(), itemId);
    }
    @MutationMapping
    public OrderItem updateItemInBasket(@Argument Integer funkoId, @Argument Integer amount) {
        return orderService.updateItemInBasket(userService.getAutentificatedUser().getId(), funkoId, amount);
    }
    @MutationMapping
        public Order createOrder(@Argument Integer prise) throws IOException {
        return payPalService.createOrder(userService.getAutentificatedUser().getId(), prise.toString());
    }
    @MutationMapping
    public Order completeOrder(@Argument String token, @Argument String payerId ) throws IOException{
        return payPalService.captureOrder(token,payerId);
    }
}
