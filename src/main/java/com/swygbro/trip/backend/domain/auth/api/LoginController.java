package com.swygbro.trip.backend.domain.auth.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swygbro.trip.backend.domain.auth.application.LoginService;
import com.swygbro.trip.backend.domain.auth.dto.LoginRequest;
import com.swygbro.trip.backend.domain.auth.dto.OAuth2LoginRequest;
import com.swygbro.trip.backend.global.jwt.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
@Tag(name = "Auth", description = """
                
        - 로그인 기능을 제공하는 API 입니다.
                
        ## 주요 기능
                
        - 로그인
        - 구글 소셜 로그인(구현 예정) 
                
        """)
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    @PostMapping
    @Operation(summary = "로그인", description = """
                        
            # 로그인
                        
            사용자의 이메일과 비밀번호를 입력하여 로그인합니다.
                        
            ## 응답
                        
            - 로그인 성공 시 `200` 코드와 함께 토큰을 반환합니다.
              - 토큰은 `access_token`과 `refresh_token`으로 구성되어 있습니다.
            - 로그인 실패 시 `400` 에러를 반환합니다. 
                        
            """)
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공 시 토큰을 반환합니다."
    )
    public TokenDto login(@Valid @RequestBody LoginRequest dto) {
        return loginService.login(dto);
    }

    @PostMapping(value = "/google")
    @Operation(summary = "구글 소셜 로그인", description = """
                        
            # 구글 소셜 로그인
                        
            구글 소셜 로그인을 통해 로그인합니다.
            클라이언트에서 구글 로그인 후 발급받은 code를 인자로 전달하면 소셜 로그인이 진행됩니다.
             s
                        
            ## 응답
                        
            - 로그인 성공 시 `200` 코드와 함께 토큰을 반환합니다.
              - 토큰은 `access_token`과 `refresh_token`으로 구성되어 있습니다.
            - 로그인 실패 시 `400` 에러를 반환합니다. 
                        
            """)
    public TokenDto googleLogin(@RequestBody OAuth2LoginRequest code) throws JsonProcessingException {
        return loginService.loginGoogle(code.getCode());
    }
}
