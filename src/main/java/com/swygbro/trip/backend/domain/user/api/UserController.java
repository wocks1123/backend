package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.dto.UpdateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import com.swygbro.trip.backend.global.document.ForbiddenResponse;
import com.swygbro.trip.backend.global.document.InvalidTokenResponse;
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
@Tag(name = "Users", description = """
        ### 개요
                
        - 회원 관련 기능을 제공합니다.
                
        ### 주요 기능
                
        - 회원 정보 조회
        - 회원 정보 수정

        """)
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 조회", description = "사용자의 프로필을 조회합니다.")
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
    @Operation(summary = "사용자 정보 수정", description = "사용자의 프로필을 수정합니다.")
    @SecurityRequirement(name = "access-token")
    @Parameters({
            @Parameter(name = "userId", description = "사용자 고유 번호", required = true, example = "1")
    })
    @ApiResponse(
            responseCode = "200",
            description = "사용자 정보 수정 성공"
    )
    @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "사용자를 찾을 수 없습니다.",
                            value = "{\"status\": \"NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\"}"
                    )
            )
    )
    @ForbiddenResponse
    @InvalidTokenResponse
    public void updateUser(@PathVariable Long userId,
                           @RequestPart @Valid UpdateUserRequest dto,
                           @RequestPart(required = false) MultipartFile imageFile) {
        userService.updateUser(userId, dto, imageFile);
    }

}
