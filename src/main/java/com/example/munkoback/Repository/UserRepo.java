package com.example.munkoback.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.munkoback.Model.User.User;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
