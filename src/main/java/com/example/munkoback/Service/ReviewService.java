package com.example.munkoback.Service;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Repository.ReviewRepository;
import com.example.munkoback.Repository.UserRepo;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository repository;
    private final UserRepo userRepo;

    private String extractEmailFromToken() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return Jwts.parser().parseClaimsJws(token).getBody().getSubject();
    }

    public List<Review> getAllReviews(){
        return repository.findAll();
    }
    public List<Review> getFunkoReviews(Integer funkoId){
        return repository.findAllByFunkoId(funkoId);
    }
    @PreAuthorize("hasAuthority('USER')")
    public Review saveReview(Review entity){
        int id =  userRepo.findByEmail(extractEmailFromToken()).get().getId();
        entity.setUserId(id);
        if(entity.getStar() < 0 || entity.getStar() > 5 ) return null;
        if(getAllReviews().contains(repository.findByFunkoIdAndUserId(entity.getFunkoId(),entity.getUserId()))) return null;
        return repository.save(entity);
    }
    @PreAuthorize("hasAuthority('USER')")
    public Review updateReview(Review entity){
        String email = extractEmailFromToken();
        if(entity.getId() == null || entity.getStar() < 0 || entity.getStar() > 5) {
            return null;
        }
        Review existing = repository.findById(entity.getId()).orElse(null);
        if(existing == null || !userRepo.findById(existing.getUserId()).get().getEmail().equals(email)){
            return null;
        }
        existing.setReview(entity.getReview());
        existing.setStar(entity.getStar());
        return repository.save(existing);
    }
    @PreAuthorize("hasAuthority('USER')")
    public Boolean deleteReview(Integer id){
        String email = extractEmailFromToken();
        if(!userRepo.findById(repository.findById(id).get().getUserId()).get().getEmail().equals(email)){
            return null;
        }
        repository.deleteById(id);
        return repository.findById(id).orElse(null) == null;
    }
}
