package com.example.munkoback.Resolver;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Service.ReviewService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReviewQueryResolver {
    private final ReviewService service;

    public ReviewQueryResolver(ReviewService service) {
        this.service = service;
    }
    @QueryMapping
    public List<Review> getAllReviews(){
        return service.getAllReviews();
    }
    @QueryMapping
    public List<Review> getFunkoReviews(@Argument Integer funkoId){
        return service.getFunkoReviews(funkoId);
    }
}
