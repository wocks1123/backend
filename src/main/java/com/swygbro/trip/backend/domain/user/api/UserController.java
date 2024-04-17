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
@Tag(name = "Users", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "사용자 생성", description = "사용자를 생성합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "사용자 생성 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = String.class, example = "user01")))
    @ApiResponse(
            responseCode = "409",
            description = "계정 중복",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = "{\n  \"status\": \"CONFLICT\",\n  \"message\": \"중복된 계정입니다.\"\n}")
            )
    )
    @ValidationErrorResponse
    public String createUser(@Valid @RequestBody CreateUserRequest dto) {
        return userService.createUser(dto);
    }

    @GetMapping("/{account}")
    @Operation(summary = "사용자 조회", description = "사용자의 프로필을 조회합니다.")
    @Parameters({
            @Parameter(name = "account", description = "사용자 계정", required = true, example = "user01")
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
    public UserProfileDto getUser(@PathVariable String account) {
        return userService.getUser(account);
    }

}
