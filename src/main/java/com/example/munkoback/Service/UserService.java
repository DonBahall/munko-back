package com.example.munkoback.Service;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import com.example.munkoback.Model.InvalidArgumentsException;
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


import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class UserService extends DefaultOAuth2UserService {

    private final UserRepo repository;
    private final JwtService jwtService;
    private final FunkoPopService funkoPopService;
    private final AuthenticationManager authenticationManager;
    @Value("${GOOGLE_TOKENINFO_URL}")
    private String GOOGLE_TOKENINFO_URL;
    @Value("${EMAIL_REGEX}")
    private String EMAIL_REGEX;
    private final EmailService emailService;
    private Map<String, String> passwordResetTokens = new HashMap<>();

    public String forgotPassword(String email) {
        User user = findByEmail(email);

        if (user == null) {
            return "Email address not found.";
        }

        String token = createPasswordResetToken(user);
        String resetLink = "https://munko-front.vercel.app/?token=" + token;

        emailService.forgotPassword(email,
                "To reset your password, click the link below:\n" + resetLink);

        return "Password reset link has been sent to your email.";
    }

    public String createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, user.getEmail());
        return token;
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public boolean resetPassword(String token, String newPassword) {
        String email = passwordResetTokens.get(token);

        if (email == null) {
            return false;
        }

        User user = findByEmail(email);
        if (user != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword);
            repository.save(user);
            passwordResetTokens.remove(token);
            return true;
        }

        return false;
    }

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
            throw new InvalidArgumentsException("This email already existing");
        }
        if (!request.getEmail().matches(EMAIL_REGEX) || request.getFirstName().equals("")) {
            throw new InvalidArgumentsException("Arguments in wrong format!");
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
                    user.setRole(Role.USER);
                }
                user.setGoogleAccountId(providerAccountId);
                repository.save(user);
            }

            return new UserRequest(user, jwtService.generateToken(user));
        } else {
            throw new InvalidArgumentsException("Invalid arguments");
        }
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public User updateUser(User request) {
        User user = getAutentificatedUser();
        if (request.getId() == null || !user.getId().equals(request.getId())) {
            throw new InvalidArgumentsException("Wrong Arguments");
        }
        User existing = repository.findById(request.getId()).orElse(null);
        if (existing == null) {
            throw new InvalidArgumentsException("User does not exist");
        }
        if (existing.getFirstName() != null && !existing.getFirstName().equals("")) {
            existing.setFirstName(request.getFirstName());
        } else {
            throw new InvalidArgumentsException("Incorrect firstname");
        }
        if (!existing.getLastName().equals("")) {
            existing.setLastName(request.getLastName());
        } else {
            throw new InvalidArgumentsException("Lastname can not be empty!");
        }
        if (existing.getEmail() != null && existing.getEmail().matches(EMAIL_REGEX)) {
            existing.setEmail(request.getEmail());
        } else {
            throw new InvalidArgumentsException("Incorrect email");
        }

        existing.setPhone(request.getPhone());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        existing.setPassword(passwordEncoder.encode(request.getPassword()));

        existing.setAddress(request.getAddress());
        existing.setOrders(request.getOrders());

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
        throw new InvalidArgumentsException("User not found");
    }

    public List<FunkoPop> getUserFavorite(Integer id) {
        User user = repository.findById(id).orElse(null);
        if (user != null) {
            return funkoPopService.getUserFavorite(user.getFavorite());
        }else return null;
    }
}
