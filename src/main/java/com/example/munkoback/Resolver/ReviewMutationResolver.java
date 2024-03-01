package com.example.munkoback.Resolver;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Service.ReviewService;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewMutationResolver {
    private final ReviewService service;

    public ReviewMutationResolver(ReviewService service) {
        this.service = service;
    }
    @MutationMapping
    public Review save(Review entity){
        return service.save(entity);
    }
    @MutationMapping
    public Review updateReview(Review entity){
        return service.updateReview(entity);
    }
    @MutationMapping
    public Boolean deleteReview(Integer entity){
        return service.deleteReview(entity);
    }
}
