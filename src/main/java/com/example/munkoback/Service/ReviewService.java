package com.example.munkoback.Service;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    @PreAuthorize("hasAuthority('USER')")
    public Review save(Review entity){
        if(getAllReviews().contains(repository.findByFunkoIdAndUserId(entity.getFunkoId(),entity.getUserId()))) return null;
        return repository.save(entity);
    }
    @PreAuthorize("hasAuthority('USER')")
    public Review updateReview(Review entity, Integer currentUserId){
        if(entity.getId() == null) {
            return null;
        }
        Review existing = repository.findById(entity.getId()).orElse(null);
        if(existing == null){
            return null;
        }
        if (!Objects.equals(existing.getUserId(), currentUserId)) {
            return null;
        }
        existing.setReview(entity.getReview());
        existing.setStar(entity.getStar());
        return repository.save(existing);
    }
    @PreAuthorize("hasAuthority('USER')")
    public Boolean deleteReview(Integer id, Integer currentUserId){
        if (!Objects.equals(repository.findById(id).get().getUserId(), currentUserId)) {
            return null;
        }
        repository.deleteById(id);
        return repository.findById(id).orElse(null) == null;
    }
}
