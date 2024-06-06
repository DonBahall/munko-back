package com.example.munkoback.Service;

import com.example.munkoback.Model.InvalidArgumentsException;
import com.example.munkoback.Model.Review;
import com.example.munkoback.Model.User.User;
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
    private final UserService userService;

    public List<Review> getAllReviews(){
        return repository.findAll();
    }
    public List<Review> getFunkoReviews(Integer funkoId){
        return repository.findAllByFunkoId(funkoId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Review saveReview(Review entity){
        if(getAllReviews().contains(repository.findByFunkoIdAndUserId(entity.getFunkoId(), entity.getUserId()))) {
            throw new InvalidArgumentsException("User already have a review");
        }
        if(entity.getStar() < 0 || entity.getStar() > 5 ) throw new InvalidArgumentsException("Invalid arguments");;
        User user = userService.getAutentificatedUser();
        if(!Objects.equals(user.getId(), entity.getUserId())){
            entity.setUserId(user.getId());
        }
        return repository.save(entity);
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Review updateReview(Review entity){
        User user = userService.getAutentificatedUser();
        if(entity.getId() == null || entity.getStar() < 0 || entity.getStar() > 5) {
            throw new InvalidArgumentsException("Invalid arguments");
        }
        Review existing = repository.findById(entity.getId()).orElse(null);
        if(existing == null || !userService.findById(existing.getUserId()).getEmail().equals(user.getEmail())){
            throw new InvalidArgumentsException("Wrong user");
        }
        existing.setReview(entity.getReview());
        existing.setStar(entity.getStar());
        return repository.save(existing);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Boolean deleteReview(Integer id){
        User user = userService.getAutentificatedUser();
        if(!userService.findById(repository.findById(id).get().getUserId()).getEmail().equals(user.getEmail())){
            throw new InvalidArgumentsException("Wrong user");
        }
        repository.deleteById(id);
        return repository.findById(id).orElse(null) == null;
    }
}
