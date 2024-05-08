package com.swygbro.trip.backend.domain.user.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swygbro.trip.backend.domain.user.dto.GoogleUserInfo;
import com.swygbro.trip.backend.global.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class GoogleOauthService {

    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String secretPassword;
    @Value("${google.client.redirect}")
    private String redirectUri;
    @Value("${google.client.authorization_uri}")
    private String authorizationUri;
    @Value("${google.client.scope}")
    private String scope;

    private final UserService userService;
    private final TokenService tokenService;

    private final RestTemplate restTemplate;


    public String getGoogleLoginUrl() {
        return UriComponentsBuilder.fromUriString(authorizationUri)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .build().toUriString();
    }

    public ResponseEntity<?> callback(String code) throws JsonProcessingException {
        String googleAccessToken = getGoogleAccessToken(code);
        GoogleUserInfo userInfo = getGoogleUserInfo(googleAccessToken);

        boolean exists = userService.existsByEmail(userInfo.getEmail());

        // 등록된 회원이라면 로그인처리
        if (exists) {
            return ResponseEntity.ok(tokenService.generateToken(userInfo.getEmail()));
        }

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .body(userInfo);
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
