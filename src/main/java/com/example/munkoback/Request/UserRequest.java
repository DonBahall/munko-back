package com.example.munkoback.Request;

import com.example.munkoback.Model.User.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    User user;
    String token;
}
