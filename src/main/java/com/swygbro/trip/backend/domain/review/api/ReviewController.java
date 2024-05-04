package com.swygbro.trip.backend.domain.review.api;


import com.swygbro.trip.backend.domain.review.application.ReviewService;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.document.ForbiddenResponse;
import com.swygbro.trip.backend.global.document.InvalidTokenResponse;
import com.swygbro.trip.backend.global.document.ValidationErrorResponse;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = """
        가이드 상품 리뷰 API
        """)
public class ReviewController {

    private final ReviewService reviewService;

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
            | guideProductId | 가이드 상품 ID | 필수 | 1 |
            | content | 리뷰 내용 | 선택, 최대 500자 | "정말 좋은 가이드입니다." | 
            | rating | 별점 | 선택, 1~5의 정수만 가능 | 5 |
                        
            ## 응답
                        
            - 
                        
            """)
    @SecurityRequirement(name = "access-token")
    @ApiResponse(
            responseCode = "201",
            description = """
                    응답 헤더의 location에 생성된 리뷰의 URI가 포함됩니다.
                    - URI 형식 : `/api/v1/reviews/{reviewId}`
                    """
    )
    @ValidationErrorResponse
    @ForbiddenResponse
    @InvalidTokenResponse
    public ResponseEntity<?> createReview(@Valid @RequestPart CreateReviewRequest dto,
                                          @RequestPart(value = "file", required = false) List<MultipartFile> images,
                                          @CurrentUser User user) {
        return ResponseEntity.created(
                URI.create("/api/v1/reviews/" + reviewService.createReview(dto, user, images))
        ).build();
    }

}

