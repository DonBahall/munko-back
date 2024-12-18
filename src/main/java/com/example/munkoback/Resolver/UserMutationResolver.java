package com.example.munkoback.Resolver;

import com.example.munkoback.Model.User.User;
import com.example.munkoback.Request.UserRequest;
import com.example.munkoback.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserMutationResolver {

    private final UserService service;
    private final String BUCKET = "munkobucket";
    @Autowired
    private final S3Client s3;

    @RequestMapping(value = "/rest/v1/upload", method = RequestMethod.POST)
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        if (fileName == null) return ("Filename cannot be null");
        if (!fileName.contains(".png") && !fileName.contains(".jpg") && !fileName.contains(".jpeg")) {
            return ("File must be a PNG or JPEG file");
        }
        User user = service.getAutentificatedUser();
        String key = user.getId().toString() + ".png";

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(key)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return "File downloaded: " + key;
        } catch (IOException e) {
            return "Error downloading file: " + e.getMessage();
        }
    }

    @RequestMapping(value = "/rest/v1/getFile", method = RequestMethod.GET)
    public ResponseEntity<String> handleGetFile() {
        User user = service.getAutentificatedUser();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(user.getId().toString() + ".png")
                .build();

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();


        Duration duration = Duration.ofHours(6);
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(b -> b
                .getObjectRequest(getObjectRequest)
                .signatureDuration(duration));


        String url = presignedRequest.url().toString();

        return ResponseEntity.ok(url);
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
