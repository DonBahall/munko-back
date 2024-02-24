package com.example.munkoback.Repository;

import com.example.munkoback.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findAllByFunkoId(Integer funkoId);
    Review findByFunkoIdAndUserId(Integer funkoId, Integer userId);
}
