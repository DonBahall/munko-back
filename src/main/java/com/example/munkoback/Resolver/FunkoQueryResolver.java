package com.example.munkoback.Resolver;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import com.example.munkoback.Model.FunkoPop.FunkoPops;
import com.example.munkoback.Model.FunkoPop.FunkoSearchCriteria;
import com.example.munkoback.Model.Paging_Sorting.OrderBy;
import com.example.munkoback.Model.Paging_Sorting.SearchPaging;
import com.example.munkoback.Service.FunkoPopService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;


@Controller
@CrossOrigin(origins = {"http://localhost:3000", "https://munko-front.vercel.app"})
public class FunkoQueryResolver {
    private final FunkoPopService service;
    @Autowired
    public FunkoQueryResolver(FunkoPopService service) {
        this.service = service;
    }
    @QueryMapping
    public FunkoPops getAllItems(@Argument SearchPaging paging,@Argument OrderBy orderBy,@Argument FunkoSearchCriteria searchCriteria ){
        return service.getAllItems(paging, orderBy, searchCriteria);
    }

    @QueryMapping
    public FunkoPop getItem(@Argument Integer id){
        return service.getItem(id);
    }
}
