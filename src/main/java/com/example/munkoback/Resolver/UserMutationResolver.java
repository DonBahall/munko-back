package com.example.munkoback.Resolver;

import com.example.munkoback.Model.User.User;
import com.example.munkoback.Request.UserRequest;
import com.example.munkoback.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserMutationResolver {

    private final UserService service;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file) {
        User user = service.getAutentificatedUser();
        if (!file.isEmpty()) {
            try {
                Path uploadDir = Paths.get("src/main/resources/static/user_photo");
                Path filePath = uploadDir.resolve(user.getId() + ".jpg");
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("File with the same name was deleted " + filePath);
                }
                Files.write(filePath, file.getBytes());

                return "Succesfully" + " -uploaded !";
            } catch (Exception e) {
                return "Error " + " => " + e.getMessage();
            }
        } else {
            return "Error";
        }
    }

    @MutationMapping
    public User registration(@Argument User user) {
        return service.registerUser(user);
    }

    @MutationMapping
    public UserRequest googleAuth(@Argument String idToken, @Argument String providerAccountId) {
        return service.googleAuth(idToken, providerAccountId);
    }

    @MutationMapping
    public User updateUser(@Argument User user) {
        return service.updateUser(user);
    }

    @MutationMapping
    public String forgotPassword(@Argument String email) {
        return service.forgotPassword(email);
    }

    @MutationMapping
    public User changePassword(@Argument String oldPassword, @Argument String newPassword) {
        return service.changePassword(oldPassword, newPassword);
    }

    @MutationMapping
    public Boolean resetPassword(@Argument String reset_token, @Argument String newPassword) {
        return service.resetPassword(reset_token, newPassword);
    }

    @MutationMapping
    public String emailConfirmation(@Argument Integer userId, @Argument String email) {
        return service.emailConfirmation(userId, email);
    }

    @MutationMapping
    public Boolean enableAccount(@Argument String email_confirm_token) {
        return service.enableAccount(email_confirm_token);
    }

    @MutationMapping
    public Boolean changeEmail(@Argument String token, @Argument String email) {
        return service.changeEmail(token, email);
    }

}
