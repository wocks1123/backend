package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.dto.UpdateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import com.swygbro.trip.backend.global.document.ForbiddenResponse;
import com.swygbro.trip.backend.global.document.InvalidTokenResponse;
import com.swygbro.trip.backend.global.document.ValidationErrorResponse;
import com.swygbro.trip.backend.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "회원 정보 조회, 수정 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 조회", description = """
                        
            응답에 포함되는 `guideProducts`는 사용자가 등록한 여행 상품 목록을 최신 4개 가져옵니다.
            추후 수정될 수 있습니다.
            ---
                        
            사용자의 프로필을 조회합니다.
                        
            ## 응답
                        
            - 사용자 조회 성공 시 `200` 코드와 함께 사용자 정보를 반환합니다.
            - 사용자 조회 실패 시 `404` 에러를 반환합니다.
                        
            """)
    @Parameters({
            @Parameter(name = "userId", description = "사용자 고유 번호", required = true, example = "1")
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
    public UserProfileDto getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

    @PutMapping(value = "{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') and #userId == principal.id")
    @Operation(summary = "사용자 정보 수정", description = """
                        
            사용자 정보 중 프로필이미지, 자기소개, 닉네임, 가능한 언어를 수정합니다.
                        
            ## 요청 양식
                        
            - 수정할 정보를 담은 json과 프로필 이미지로 사용할 파일을 함께 보냅니다.
                        
            | 필드명 | 설명 | 제약조건 |  예시  |
            |--------|------|----------|--------|
            | profile | 자기소개 | 500자 이내 | "안녕하세요" |
            | nickname | 닉네임 | 4~20자 | "nickname_01" |
            | languages | 사용 가능한 언어 목록을 ISO 639-1 형식으로 표현 | ISO 639-1 형식 | ["ko", "en"] |
                        
            ## 응답
                        
            - 사용자 정보 수정 성공 시 `200` 코드를 반환합니다.
            - 입력 양식 오류 시 `400` 에러를 반환합니다.
            - 토큰이 유효하지 않을 경우 `401` 에러를 반환합니다.
            - 권한이 없을 경우 `403` 에러를 반환합니다.

            """)
    @SecurityRequirement(name = "access-token")
    @Parameters({
            @Parameter(name = "userId", description = "사용자 고유 번호", required = true, example = "1")
    })
    @ApiResponse(
            responseCode = "200",
            description = "사용자 정보 수정 성공"
    )
    @ValidationErrorResponse
    @ForbiddenResponse
    @InvalidTokenResponse
    public void updateUser(@PathVariable Long userId,
                           @RequestPart @Valid UpdateUserRequest dto,
                           @RequestPart(required = false) MultipartFile imageFile) {
        userService.updateUser(userId, dto, imageFile);
    }

}
