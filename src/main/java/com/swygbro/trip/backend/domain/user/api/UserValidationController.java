package com.swygbro.trip.backend.domain.user.api;

import com.swygbro.trip.backend.domain.user.application.UserValidationService;
import com.swygbro.trip.backend.domain.user.util.FieldName;
import com.swygbro.trip.backend.global.document.ValidationErrorResponse;
import com.swygbro.trip.backend.global.validation.Phone;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/validation")
@RequiredArgsConstructor
@Tag(name = "Users Validation", description = "회원가입 시 입력 값 중복을 확인하는 API")
public class UserValidationController {

    private final UserValidationService userValidationService;


    @GetMapping("/email")
    @Operation(summary = "이메일 중복 확인", description = "이메일 중복을 확인합니다.")
    @Parameter(name = "email", description = "사용자 이메일", required = true, example = "email01@email.com")
    @ApiResponse(responseCode = "200", description = "중복된 이메일이 없습니다.")
    @ValidationErrorResponse
    @ApiResponse(responseCode = "409", description = "이메일 중복")
    public void checkEmailDuplication(@Valid @NotBlank @Email
                                      @RequestParam String email) {
        userValidationService.checkDuplicationData(FieldName.Email, email);
    }

    @GetMapping("/nickname")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복을 확인합니다.")
    @Parameter(name = "nickname", description = "사용자 닉네임", required = true, example = "nickname01")
    @ApiResponse(responseCode = "200", description = "중복된 닉네임이 없습니다.")
    @ValidationErrorResponse
    @ApiResponse(responseCode = "409", description = "닉네임 중복")
    public void checkNicknameDuplication(@NotBlank @Size(max = 20)
                                         @RequestParam String nickname) {
        userValidationService.checkDuplicationData(FieldName.Nickname, nickname);
    }

    @GetMapping("/phone")
    @Operation(summary = "전화번호 중복 확인", description = "전화번호 중복을 확인합니다. 전화번호 양식은 '-'를 제외한 숫자입니다.")
    @Parameter(name = "phone", description = "사용자 전화번호", required = true, example = "01012345678")
    @ApiResponse(responseCode = "200", description = "중복된 전화번호가 없습니다.")
    @ValidationErrorResponse
    @ApiResponse(responseCode = "409", description = "전화번호 중복")
    public void checkPhoneDuplication(@NotBlank @Size(max = 20) @Phone
                                      @RequestParam String phone) {
        userValidationService.checkDuplicationData(FieldName.Phone, phone);
    }

}
