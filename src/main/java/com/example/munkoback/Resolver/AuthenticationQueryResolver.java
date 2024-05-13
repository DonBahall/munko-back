package com.example.munkoback.Resolver;
import com.example.munkoback.Model.User.User;
import com.example.munkoback.Request.AuthenticationRequest;
import com.example.munkoback.Request.UserRequest;
import com.example.munkoback.Service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthenticationQueryResolver {

    private final UserService service;
    @QueryMapping
    public UserRequest authenticate(@Argument String email, @Argument String password) {
        return service.authenticate(new AuthenticationRequest(email, password));
    }
    @QueryMapping
    public User getCurrentUser(){
        return service.getAutentificatedUser();
    }
    @MutationMapping
    public User registration(@Argument User user){
        return service.registerUser(user);
    }
    @MutationMapping
    public User updateUser(@Argument User user){
        return service.updateUser(user);
    }

}
