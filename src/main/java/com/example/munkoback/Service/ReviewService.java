package com.example.munkoback.Service;

import com.example.munkoback.Model.Review;
import com.example.munkoback.Model.User;
import com.example.munkoback.Repository.ReviewRepository;
import com.example.munkoback.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository repository;
    private final UserRepo userRepo;
    private final AuthenticationService authenticationService;

    public List<Review> getAllReviews(){
        return repository.findAll();
    }
    public List<Review> getFunkoReviews(Integer funkoId){
        return repository.findAllByFunkoId(funkoId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Review saveReview(Review entity){
        User user = authenticationService.getAutentificatedUser();
        entity.setUserId(user.getId());
        if(entity.getStar() < 0 || entity.getStar() > 5 ) return null;
        if(getAllReviews().contains(repository.findByFunkoIdAndUserId(entity.getFunkoId(),entity.getUserId()))) return null;
        return repository.save(entity);
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Review updateReview(Review entity){
        User user = authenticationService.getAutentificatedUser();
        if(entity.getId() == null || entity.getStar() < 0 || entity.getStar() > 5) {
            return null;
        }
        Review existing = repository.findById(entity.getId()).orElse(null);
        if(existing == null || !userRepo.findById(existing.getUserId()).get().getEmail().equals(user.getEmail())){
            return null;
        }
        existing.setReview(entity.getReview());
        existing.setStar(entity.getStar());
        return repository.save(existing);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Boolean deleteReview(Integer id){
        User user = authenticationService.getAutentificatedUser();
        if(!userRepo.findById(repository.findById(id).get().getUserId()).get().getEmail().equals(user.getEmail())){
            return null;
        }
        repository.deleteById(id);
        return repository.findById(id).orElse(null) == null;
    }
}
