package com.example.munkoback.Service;
import com.example.munkoback.Model.User.User;

import java.io.IOException;
import com.google.api.services.walletobjects.Walletobjects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GooglePayService {

    private final Walletobjects walletobjects;
    private final UserService userService;
    public String createGooglePayOrder(Integer userId, String price) throws IOException {
        User user = userService.findById(userId);
        return "";
    }
}
