package com.example.munkoback.Service;

import com.example.munkoback.Model.User;
import com.example.munkoback.Repository.TokenRepo;
import com.example.munkoback.Repository.UserRepo;
import com.example.munkoback.Request.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo repository;
    private final TokenRepo tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public String authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        revokeAllUserTokens(user);
        return jwtService.generateToken(user);
    }

    public User registerUser(User request) {
        if (repository.existsByEmail(request.getEmail())) {
            return request;
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);

        return repository.save(request);
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateUser(User request){
        if(request.getId() == null) {
           return null;
        }
        User existing = repository.findById(request.getId()).orElse(null);
        if(existing == null){
            return null;
        }
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        existing.setPassword(passwordEncoder.encode(request.getPassword()));

        existing.setPassword(request.getPassword());
        existing.setAddress(request.getAddress());
        existing.setOrders(request.getOrders());
        existing.setRole(request.getRole());
        existing.setFavorite(request.getFavorite());

       return repository.save(existing);
    }

    public User getAutentificatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
