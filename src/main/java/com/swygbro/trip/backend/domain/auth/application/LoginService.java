package com.swygbro.trip.backend.domain.auth.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swygbro.trip.backend.domain.auth.dto.GoogleUserInfo;
import com.swygbro.trip.backend.domain.auth.dto.LoginRequest;
import com.swygbro.trip.backend.domain.auth.exception.LoginFailException;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.global.jwt.TokenService;
import com.swygbro.trip.backend.global.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;


    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String secretPassword;
    @Value("${google.client.redirect}")
    private String redirectUri;

    public TokenDto login(LoginRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(LoginFailException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new LoginFailException();
        }

        return tokenService.generateToken(user.getEmail());
    }

    public TokenDto loginGoogle(String code) throws JsonProcessingException {
        String accessToken = getGoogleAccessToken(code);
        GoogleUserInfo userInfo = getGoogleUserInfo(accessToken);
        // TODO 회원등록 추가
        return tokenService.generateToken(userInfo.getEmail());
    }

    public String getGoogleAccessToken(String code) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", secretPassword);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity.post(uri).headers(headers).body(body);
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    private GoogleUserInfo getGoogleUserInfo(String googleAccessToken) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v2/userinfo")
                .queryParam("access_token", googleAccessToken)
                .encode()
                .build()
                .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        return new ObjectMapper().readValue(responseEntity.getBody(), GoogleUserInfo.class);
    }

}
