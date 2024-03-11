package com.example.munkoback.Resolver;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Service.ReviewService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Component;


@Component
public class ReviewMutationResolver {
    private final ReviewService service;

    public ReviewMutationResolver(ReviewService service) {
        this.service = service;
    }
    @MutationMapping
    public Review save(@Argument Review entity){
        return service.saveReview(entity);
    }
    @MutationMapping
    public Review updateReview(@Argument Review entity){
        return service.updateReview(entity);
    }
    @MutationMapping
    public Boolean deleteReview(@Argument Integer entity){
        return service.deleteReview(entity);
    }
}
