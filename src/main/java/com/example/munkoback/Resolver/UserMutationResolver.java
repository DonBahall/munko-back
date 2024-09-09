package com.example.munkoback.Resolver;

import com.example.munkoback.Model.User.User;
import com.example.munkoback.Request.UserRequest;
import com.example.munkoback.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Controller
@RequiredArgsConstructor
public class UserMutationResolver {

    private final UserService service;

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
            @RequestParam("file") MultipartFile file){
        User user = service.getAutentificatedUser();
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("photos/" + user.getId() + ".jpg")));
                stream.write(bytes);
                stream.close();

                return "Вы удачно загрузили " + " -uploaded !";
            } catch (Exception e) {
                return "Вам не удалось загрузить "+ " => " + e.getMessage();
            }
        } else {
            return "Вам не удалось загрузить " + " потому что файл пустой.";
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
        return service.changePassword(oldPassword,newPassword);
    }

    @MutationMapping
    public Boolean resetPassword(@Argument String reset_token, @Argument String newPassword) {
        return service.resetPassword(reset_token,newPassword);
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
        return service.changeEmail(token,email);
    }

}
