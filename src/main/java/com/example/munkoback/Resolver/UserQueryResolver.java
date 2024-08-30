package com.example.munkoback.Resolver;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import com.example.munkoback.Model.Order.Order;
import com.example.munkoback.Model.User.User;
import com.example.munkoback.Request.AuthenticationRequest;
import com.example.munkoback.Request.UserRequest;
import com.example.munkoback.Service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserQueryResolver {

    private final UserService service;

    @QueryMapping
    public UserRequest authenticate(@Argument String email, @Argument String password) {
        return service.authenticate(new AuthenticationRequest(email, password));
    }

    @QueryMapping
    public User getCurrentUser() {
        return service.getAutentificatedUser();
    }

    @QueryMapping
    public List<Order> getUserOrders() {
        return service.getUserOrders(service.getAutentificatedUser().getId());
    }

    @QueryMapping
    public List<FunkoPop> getUserFavorite() {
        return service.getUserFavorite(service.getAutentificatedUser().getId());
    }

    @QueryMapping
    public Boolean deleteAccount() {
        return service.deleteAccount();
    }

}
