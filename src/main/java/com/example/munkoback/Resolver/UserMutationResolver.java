package com.example.munkoback.Resolver;

import com.example.munkoback.Model.User.User;
import com.example.munkoback.Request.UserRequest;
import com.example.munkoback.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserMutationResolver {

    private final UserService service;

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
        return service.changePassword(oldPassword,newPassword);
    }

    @MutationMapping
    public Boolean resetPassword(@Argument String reset_token, @Argument String newPassword) {
        return service.resetPassword(reset_token,newPassword);
    }

    @MutationMapping
    public String emailConfirmation(@Argument Integer userId) {
        return service.emailConfirmation(userId);
    }

    @MutationMapping
    public Boolean enableAccount(@Argument String email_confirm_token) {
        return service.enableAccount(email_confirm_token);
    }

}
