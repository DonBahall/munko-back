package com.example.munkoback.Service;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import com.example.munkoback.Model.InvalidArgumentsException;
import com.example.munkoback.Model.Order.Order;

import com.example.munkoback.Model.User.Address;
import com.example.munkoback.Model.User.CreditCard;
import com.example.munkoback.Model.User.Role;
import com.example.munkoback.Model.User.User;
import com.example.munkoback.Repository.CreditCardRepository;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final CreditCardRepository creditCardRepository;
    private final AuthenticationManager authenticationManager;
    @Value("${GOOGLE_TOKENINFO_URL}")
    private String GOOGLE_TOKENINFO_URL;
    @Value("${EMAIL_REGEX}")
    private String EMAIL_REGEX;
    private final EmailService emailService;
    private Map<String, String> passwordResetTokens = new HashMap<>();
    private Map<String, String> confirmTokens = new HashMap<>();

    public String forgotPassword(String email) {
        User user = findByEmail(email);

        if (user == null) {
            throw new InvalidArgumentsException("Invalid email or password ");
        }

        String token = createPasswordResetToken(user);
        String resetLink = "https://munko-front.vercel.app/?reset_token=" + token;

        emailService.forgotPassword(email,
                "To reset your password, click the link below:\n" + resetLink);

        return "Password reset link has been sent to your email.";
    }

    public User changePassword(String oldPassword, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = getAutentificatedUser();
        if (encoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(encoder.encode(newPassword));
            return repository.save(user);
        } else return null;
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
        if (user != null && user.isEnabled()) {
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

    public String emailConfirmation(Integer userId, String email) {
        User user = repository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        String token = UUID.randomUUID().toString();
        String confirmLink = "https://munko-front.vercel.app/?confirm_token=" + token;

        if (email != null) {
            confirmTokens.put(token, email);
            emailService.emailConfirmation(email, "To confirm your email, click the link below:\n" + confirmLink + "&email=" + email);
        } else {
            confirmTokens.put(token, user.getEmail());
            emailService.emailConfirmation(user.getEmail(), "To confirm your email, click the link below:\n" + confirmLink);
        }

        return "Confirmation link has been sent to your email.";
    }

    public Boolean changeEmail(String token, String newEmail) {
        String email = confirmTokens.get(token);
        if (email == null) {
            return false;
        }
        User user = getAutentificatedUser();
        if (user != null) {
            user.setEmail(newEmail);
            repository.save(user);
            confirmTokens.remove(token);
            return true;
        }
        return false;
    }

    public Boolean deleteAccount() {
        User user = getAutentificatedUser();
        if (user != null) {
            repository.delete(user);
            return true;
        }
        return false;
    }

    public Boolean enableAccount(String token) {
        String email = confirmTokens.get(token);

        if (email == null) {
            return false;
        }

        User user = findByEmail(email);
        if (user != null) {
            user.setIsEnabled(true);
            repository.save(user);
            confirmTokens.remove(token);
            return true;
        }
        return false;
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
        request.setIsEnabled(false);
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
        if (request.getFirstName() != null) {
            if (!request.getFirstName().isEmpty()) {
                existing.setFirstName(request.getFirstName());
            }
        }

        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            existing.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && request.getEmail().matches(EMAIL_REGEX)) {
            existing.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            existing.setPhone(request.getPhone());
        }
        if (request.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getAddress() != null) {
            Address address = user.getAddress();
            if (request.getAddress().getUserId() != null) {
                address.setUserId(request.getAddress().getUserId());
            }
            if (request.getAddress().getCity() != null) {
                address.setCity(request.getAddress().getCity());
            }
            if (request.getAddress().getDistrict() != null) {
                address.setDistrict(request.getAddress().getDistrict());
            }
            if (request.getAddress().getHouse() != null) {
                address.setHouse(request.getAddress().getHouse());
            }
            if (request.getAddress().getCountry() != null) {
                address.setCountry(request.getAddress().getCountry());
            }
            if (request.getAddress().getPostalCode() != null) {
                address.setPostalCode(request.getAddress().getPostalCode());
            }
            if (request.getAddress().getCity() != null) {
                address.setCity(request.getAddress().getCity());
            }
            existing.setAddress(address);
        }
        if (request.getOrders() != null) {
            existing.setOrders(request.getOrders());
        }
        if (request.getFavorite() != null) {
            existing.setFavorite(request.getFavorite());
        }
        if (request.getCreditCard() != null) {
            creditCardRepository.saveAll(request.getCreditCard());
            existing.setCreditCard(request.getCreditCard());
        }

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
        } else return null;
    }

    public void save(User user) {
        repository.save(user);
    }
}
