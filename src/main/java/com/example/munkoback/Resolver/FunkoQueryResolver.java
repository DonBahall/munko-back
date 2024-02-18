package com.example.munkoback.Resolver;

import com.example.munkoback.Model.FunkoPop;
import com.example.munkoback.Service.FunkoPopService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@SuppressWarnings("unused")
@Controller
public class FunkoQueryResolver {
    private final FunkoPopService service;
    @Autowired
    public FunkoQueryResolver(FunkoPopService service) {
        this.service = service;
    }
    @QueryMapping
    public List<FunkoPop> getAllItems(){
        return service.getAllItems();
    }
    @QueryMapping
    public FunkoPop getItem(@Argument Integer id){
        return service.getItem(id);
    }
}
