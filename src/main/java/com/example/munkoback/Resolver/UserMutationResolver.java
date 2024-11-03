package com.example.munkoback.Resolver;

import com.example.munkoback.Model.User.User;
import com.example.munkoback.Request.UserRequest;
import com.example.munkoback.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserMutationResolver {

    private final UserService service;
    private final String BUCKET = "munkobucket";
    private final S3Client s3 = S3Client.builder().region(Region.US_WEST_2).build();;

    @RequestMapping(value = "/rest/v1/upload", method = RequestMethod.POST)
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file) {
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
    public ResponseEntity<byte[]> handleGetFile() {
        User user = service.getAutentificatedUser();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(user.getId().toString())
                .build();

        ResponseBytes<GetObjectResponse> responseBytes = s3.getObjectAsBytes(getObjectRequest);
        byte[] content = responseBytes.asByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + user.getId().toString());
        headers.add(HttpHeaders.CONTENT_TYPE, "image/png");

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
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
