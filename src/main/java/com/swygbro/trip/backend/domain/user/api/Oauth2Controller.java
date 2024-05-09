package com.swygbro.trip.backend.domain.user.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.swygbro.trip.backend.domain.user.application.GoogleOauthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
@Tag(name = "Login", description = "로그인 관련 API")
public class Oauth2Controller {

    private final GoogleOauthService googleOauthService;

    @GetMapping("/google")
    @Operation(summary = "구글 로그인", description = """
            # 구글 로그인
                        
            - 이 API를 호출하면 구글 로그인 페이지로 리다이렉트됩니다.
              - 이 API를 구글 로그인 버튼의 링크로 사용합니다. 
            - 구글 로그인 페이지에서 로그인을 완료하면 콜백 URL로 리다이렉트됩니다.
            - 콜백 URL에서 구글 인가 코드를 받아서 처리합니다.
                        
            """)

    @ApiResponse(
            responseCode = "302",
            headers = {
                    @Header(
                            name = "Location",
                            description = """
                                    - 구글 로그인 페이지 URL
                                    - ex) https://accounts.google.com/o/oauth2/v2/auth?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code&scope=openid profile email
                                    """)
            },
            description = "구글 로그인 페이지로 리다이렉트됩니다.")
    public void redirectGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleOauthService.getGoogleLoginUrl());
    }

    @GetMapping("/callback")
    @Operation(summary = "구글 로그인 콜백", description = """
                        
            - 프론트엔드에서 테스트 필요
              - 301 응답 시 회원가입 페이지로 이동 및 응답 데이터를 받아서 회원가입 페이지에서 사용할 수 있는지... 

            ---

            # 구글 로그인 콜백

                        
            - 구글 로그인 페이지에서 로그인을 완료하면 구글 인가 코드와 함께 이 API로 리다이렉트됩니다.
            - 구글 인가 코드를 사용해 계정의 정보를 조회 및 처리합니다.
            - DB에 등록된 이메일이면 로그인 처리, 등록되지 않은 이메일이면 회원가입 페이지로 이동 합니다.
            - 로그인 시 200 코드와 함께 JWT 토큰을 반환합니다.
            - 회원가입 시 301 코드와 함께 회원 정보를 반환합니다.
              - 반환 정보는 추후 협의 필요 
              
            회원정보 반환 예시:
                        
            ```json
            {
              "id": "114210435473335230303",
              "name": "Mat thew",
              "given_name": "Mat",
              "family_name": "thew",
              "locale": "ko",
              "email": "swyg46@gmail.com",
              "verified_email": true,
              "picture": "https://lh3.googleusercontent.com/a/ACg8ocKrQm8D09Njibbo_vgeOQZt_oBdu63c6qOgxsWZ6QOJdcflxA=s96-c"
            }
            """)
    @Parameter(name = "code", description = "구글 인가 코드", required = true)
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공 시 JWT 토큰을 반환합니다.",
            content = @Content(mediaType = "application/json"))
    @ApiResponse(
            responseCode = "301",
            description = "회원가입 페이지",
            content = @Content(mediaType = "application/json"))
    public ResponseEntity<?> callback(@RequestParam String code) throws JsonProcessingException {
        return googleOauthService.callback(code);
    }

}
