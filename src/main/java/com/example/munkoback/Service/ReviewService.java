package com.example.munkoback.Service;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Model.User;
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
    public Review updateReview(Review entity){
        if(entity.getId() == null) {
            return null;
        }
        Review existing = repository.findById(entity.getId()).orElse(null);
        if(existing == null){
            return null;
        }
        existing.setReview(entity.getReview());
        existing.setStar(entity.getStar());
        return repository.save(existing);
    }
    public Boolean deleteReview(Integer id){
        repository.deleteById(id);
        return repository.findById(id).orElse(null) == null;
    }
}
