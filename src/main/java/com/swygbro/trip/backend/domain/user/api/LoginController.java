package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.LoginService;
import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.LoginRequest;
import com.swygbro.trip.backend.global.document.ValidationErrorResponse;
import com.swygbro.trip.backend.global.exception.ApiErrorResponse;
import com.swygbro.trip.backend.global.jwt.dto.TokenDto;
import com.swygbro.trip.backend.infra.discordbot.DiscordMessageProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Login", description = "로그인 관련 API")
public class LoginController {

    private final LoginService loginService;
    private final UserService userService;
    private final DiscordMessageProvider discordMessageProvider;


    @PostMapping("/login")
    @Operation(summary = "이메일 로그인", description = """
                        
            # 로그인
                        
            사용자의 이메일과 비밀번호를 입력하여 로그인합니다.
                        
            ## 응답
                        
            - 로그인 성공 시 `200` 코드와 함께 토큰을 반환합니다.
              - 토큰은 `access_token`과 `refresh_token`으로 구성되어 있습니다.
            - 로그인 실패 시 `400` 에러를 반환합니다. 
              - 계정이 존재하지 않거나 비밀번호가 일치하지 않을 경우 발생합니다.     
            """)
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공 시 토큰을 반환합니다.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TokenDto.class),
                    examples = @ExampleObject(value = """
                            {
                                "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMDFAZW1haWwuY29tIiwiZXhwIjoxNzE1MDA2OTc4LCJpYXQiOjE3MTUwMDMzNzh9.4mVSMEiBNmbD0VXo0w5ZtOl45Qu5V1a4dZEArkibbIQ",
                                "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMDFAZW1haWwuY29tIiwiZXhwIjoxNzE1MDg5Nzc4LCJpYXQiOjE3MTUwMDMzNzh9.GK01DHHZYxYc5FXl_c5Aq0qJg9YbUoSl-U6YCj8L7ik"
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = """
                            {
                                "status": "BAD_REQUEST",
                                "message": "로그인에 실패했습니다."
                            }
                            """)
            )
    )
    public TokenDto login(@Valid @RequestBody LoginRequest dto) {
        return loginService.login(dto);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = """
            # 회원가입
                        
            회원을 생성합니다. 회원 가입 시 사용자 이메일, 닉네임, 이름, 전화번호, 국적, 성별, 비밀번호, 비밀번호 확인을 입력합니다.
            각 필드의 제약 조건은 다음과 같습니다.

            | 필드명 | 설명 | 제약조건 | 중복확인 | 예시 |
            |--------|------|----------|----------|------|
            |email| 사용자의 이메일 | 이메일 형식 | Y | email01@email.com |
            |nickname| 다른 사용자들에게 보이는 닉네임 | 4~20자 | Y | nickname01 |            
            |name| 사용자의 이름 | 2~20자 | N | name01 |
            |phone| 사용자의 전화번호 | '-'를 제외한 숫자 | Y | 01012345678 |
            |nationality| 사용자의 국적 | 영문3자 국가 코드 | N | KOR |
            |gender| 성별 | Male, Female 중 하나 | N | Male |
            |password| 사용자의 비밀번호 | 영문(대소문자), 숫자, 특수문자를 포함한 8~32자 | N | password01! |
            |passwordCheck| 사용자의 비밀번호 확인 | password와 동일한 입력 | N | password01! |
             
            ## 응답
                        
            - 회원 가입 성공 시 `200` 코드와 함께 회원 이메일을 문자열로 반환합니다.
            - 중복된 값이 있을 경우 `409` 에러를 반환합니다.
            - 입력 양식에 오류가 있을 경우 `400` 에러를 반환합니다.
             
            """)
    @ApiResponse(
            responseCode = "200",
            description = "생성한 계정 고유 번호를 반환합니다.",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(implementation = Long.class, example = "1")))
    @ApiResponse(
            responseCode = "409",
            description = "입력 값 중 중복된 값이 있습니다.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = "{\n  \"status\": \"CONFLICT\",\n  \"message\": \"데이터 중복\"\n}")
            )
    )
    @ValidationErrorResponse
    public Long createUser(@Valid @RequestBody CreateUserRequest dto) {
        discordMessageProvider.sendMessage("회원가입이 완료되었습니다.");
        return userService.createUser(dto);
    }


}
