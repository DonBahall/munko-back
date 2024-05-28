package com.example.munkoback.Service;

import com.example.munkoback.Model.Order.Order;
import com.example.munkoback.Model.User.Role;
import com.example.munkoback.Model.User.User;
import com.example.munkoback.Repository.UserRepo;
import com.example.munkoback.Request.AuthenticationRequest;
import com.example.munkoback.Request.UserRequest;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService extends DefaultOAuth2UserService {

    private final UserRepo repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${GOOGLE_TOKENINFO_URL}")
    private String GOOGLE_TOKENINFO_URL;

    public UserRequest authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = repository.findByEmail(request.getEmail())
                .orElse(null);
        if (user != null) {
            return new UserRequest(user, jwtService.generateToken(user));
        }
        return null;
    }

    public User registerUser(User request) {
        if (repository.existsByEmail(request.getEmail())) {
            return request;
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);
        request.setRole(Role.USER);
        repository.save(request);

        return request;
    }

    public UserRequest googleAuth(String idToken, String providerAccountId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id_token", idToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKENINFO_URL, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {

            JSONObject jsonObject = new JSONObject(response.getBody());
            String name = jsonObject.getString("name");
            String email = jsonObject.getString("email");

            User user = repository.findByGoogleAccountId(providerAccountId).orElse(null);
            if (user == null) {
                user = repository.findByEmail(email).orElse(null);
                if (user == null) {
                    user = new User();
                    user.setFirstName(name);
                    user.setEmail(email);
                }
                user.setGoogleAccountId(providerAccountId);
                repository.save(user);
            }

            return new UserRequest(user, jwtService.generateToken(user));
        } else {
            return null;
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateUser(User request) {
        User user = getAutentificatedUser();
        if (request.getId() == null || !user.getId().equals(request.getId())) {
            return null;
        }
        User existing = repository.findById(request.getId()).orElse(null);
        if (existing == null) {
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

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User getAutentificatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public User findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<Order> getUserOrders(Integer userId) {
        User user = repository.findById(userId).orElse(null);
        if (user != null) {
            return user.getOrders();
        }
        return null;
    }
}
