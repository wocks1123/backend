package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.LoginService;
import com.swygbro.trip.backend.domain.user.dto.LoginRequest;
import com.swygbro.trip.backend.global.jwt.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

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


}
