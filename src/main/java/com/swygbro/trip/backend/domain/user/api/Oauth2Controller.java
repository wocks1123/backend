package com.swygbro.trip.backend.domain.user.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.swygbro.trip.backend.domain.user.application.GoogleOauthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class Oauth2Controller {

    private final GoogleOauthService googleOauthService;

    @GetMapping("/google")
    public void redirectGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleOauthService.getGoogleLoginUrl());
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code) throws JsonProcessingException {
        googleOauthService.callback(code);
    }

}
