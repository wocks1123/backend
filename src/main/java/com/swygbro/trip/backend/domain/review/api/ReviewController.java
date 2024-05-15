package com.swygbro.trip.backend.domain.review.api;


import com.swygbro.trip.backend.domain.review.application.ReviewService;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.review.dto.ReviewDetailDto;
import com.swygbro.trip.backend.domain.review.dto.ReviewInfoDto;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.document.ForbiddenResponse;
import com.swygbro.trip.backend.global.document.InvalidTokenResponse;
import com.swygbro.trip.backend.global.document.ValidationErrorResponse;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.util.List;


@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = """
        가이드 상품 리뷰 API
        """)
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("{reviewId}")
    @Operation(summary = "", description = """
                        
            """)
    @ApiResponse(
            responseCode = "200",
            description = "`200` 코드와 함께 생성한 리뷰 정보가 반환됩니다.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ReviewDetailDto.class)
            )
    )
    ReviewDetailDto getReviewById(@PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "리뷰 생성", description = """
            # 리뷰 생성
                        
            - 가이드 상품에 대한 리뷰를 생성합니다.
            - 별점은 1~5까지의 정수만 가능하며 필수로 입력해야합니다.
            - 리뷰 내용은 최대 500자까지 입력할 수 있습니다.
            - 리뷰 생성 시 이미지를 함께 업로드할 수 있으며 최대 6개까지 업로드할 수 있습니다.
            - 리뷰 작성에는 로그인이 필요합니다.

            요청 형식은 다음과 같습니다.
                        
            | 필드명 | 설명 | 제약조건 | 예시 |
            |--------|------|----------|------|
            | reservationId | 예약 ID | 필수 | 1 |
            | content | 리뷰 내용 | 선택, 최대 500자 | "정말 좋은 가이드입니다." | 
            | rating | 별점 | 선택, 1~5의 정수만 가능 | 5 |
                        
            ## 응답
                        
            - 성공 시 `200` 코드와 함께 생성한 리뷰 정보가 반환됩니다.
            - 입력 값이 잘못된 경우 `400` 코드와 함께 에러 메시지가 반환됩니다.
            - 리뷰를 작성할 수 없는 경우 `400` 코드와 함께 에러 메시지가 반환됩니다.
                - 예약 ID가 존재하지 않는 경우 `400` 코드와 함께 에러 메시지를 반환합니다.
                - 예약 상태가 `예약완료`인 경우에만 리뷰를 작성할 수 있습니다.
                - 예약 내역과 요청한 사용자가 일치해야합니다.
            - 토큰이 유효하지 않은 경우 `401` 코드와 함께 에러 메시지가 반환됩니다.
            - 로그인하지 않은 경우 `403` 코드와 함께 에러 메시지가 반환됩니다.
                        
            """)
    @SecurityRequirement(name = "access-token")
    @ApiResponse(
            responseCode = "200",
            description = "`200` 코드와 함께 생성한 리뷰 정보가 반환됩니다.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ReviewInfoDto.class)
            )
    )
    @ValidationErrorResponse
    @ForbiddenResponse
    @InvalidTokenResponse
    public ReviewInfoDto createReview(@Valid @RequestPart CreateReviewRequest dto,
                                      @RequestPart(value = "file", required = false) List<MultipartFile> images,
                                      @CurrentUser User user) {
        return reviewService.createReview(dto, user, images);
    }

}

