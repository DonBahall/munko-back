package com.example.munkoback.Resolver;
import com.example.munkoback.Request.AuthenticationRequest;
import com.example.munkoback.Service.AuthenticationService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
@Controller
@SuppressWarnings("unused")
public class AuthenticationQueryResolver implements GraphQLQueryResolver {
    private final AuthenticationService service;
    @Autowired
    public AuthenticationQueryResolver(AuthenticationService service) {
        this.service = service;
    }
    @QueryMapping
    public String authenticate(@Argument String email,@Argument String password) {
        return service.authenticate(new AuthenticationRequest(email, password));
    }

}
