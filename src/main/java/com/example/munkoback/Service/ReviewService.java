package com.example.munkoback.Service;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository repository;

    public List<Review> getAllReviews(){
        return repository.findAll();
    }
    public List<Review> getFunkoReviews(Integer funkoId){
        return repository.findAllByFunkoId(funkoId);
    }
    public Review save(Review entity){
        if(getAllReviews().contains(repository.findByFunkoIdAndUserId(entity.getFunkoId(),entity.getUserId()))) return null;
        return repository.save(entity);
    }

}
