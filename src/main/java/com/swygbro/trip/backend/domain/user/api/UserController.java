package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import com.swygbro.trip.backend.global.document.ValidationErrorResponse;
import com.swygbro.trip.backend.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = """
        ### 개요
                
        - 회원 관련 기능을 제공합니다.
                
        ### 주요 기능
                
        - 회원 가입
        - 회원 정보 조회

        """)
public class UserController {

    private final UserService userService;

    @PostMapping
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
            description = "생성한 계정의 이메일을 문자열로 반환합니다.",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(implementation = String.class, example = "user01")))
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
    public String createUser(@Valid @RequestBody CreateUserRequest dto) {
        return userService.createUser(dto);
    }

    @GetMapping("/{email}")
    @Operation(summary = "사용자 조회", description = "사용자의 프로필을 조회합니다.")
    @Parameters({
            @Parameter(name = "email", description = "이메일", required = true, example = "user01@email.com")
    })
    @ApiResponse(
            responseCode = "200",
            description = "사용자 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserProfileDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "사용자 조회 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "사용자를 찾을 수 없습니다.",
                            value = "{\"status\": \"NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\"}"
                    )
            )
    )
    public UserProfileDto getUser(@PathVariable String email) {
        return userService.getUser(email);
    }
    
}
