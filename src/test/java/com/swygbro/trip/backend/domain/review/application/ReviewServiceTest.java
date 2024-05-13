package com.swygbro.trip.backend.domain.review.application;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.review.domain.Review;
import com.swygbro.trip.backend.domain.review.domain.ReviewRepository;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.domain.*;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuideProductRepository guideProductRepository;

    @Mock
    S3Service s3Service;

    private User reviewer;
    private GuideProduct guideProduct;

    @BeforeEach
    void setUp() {
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "testemail000001@email.com",
                "testnickname0001",
                "testname0001",
                "00000000000",
                Nationality.KOR,
                "1990-01-01",
                Gender.Male,
                "password01!",
                "password01!"
        );

        reviewer = userRepository.save(new User(createUserRequest, SignUpType.Local, "password"));

        guideProduct = guideProductRepository.save(new GuideProduct(
                reviewer,
                "test title",
                "test description",
                10000L,
                127.0,
                37.0,
                ZonedDateTime.now(),
                ZonedDateTime.now()
        ));
    }

    @Test
    @DisplayName("리뷰생성 성공")
    void createReviewSuccess() {
        // given
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .guideProductId(guideProduct.getId())
                .content("test content")
                .rating(5)
                .build();
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);
        images.forEach(image -> {
            given(s3Service.uploadImage(image)).willReturn("testImageUrl" + image.getName());
        });

        reviewService.createReview(
                createReviewRequest,
                reviewer,
                images
        );

        // when
        Long reviewId = reviewService.createReview(createReviewRequest, reviewer, images);

        // then
        Review createdReview = reviewRepository.findById(reviewId).orElse(null);
        assertThat(createdReview).isNotNull();
        assertThat(createdReview.getRating()).isEqualTo(createReviewRequest.getRating());
        assertThat(createdReview.getContent()).isEqualTo(createReviewRequest.getContent());
        assertThat(createdReview.getReviewer().getId()).isEqualTo(reviewer.getId());
        assertThat(createdReview.getGuideProduct().getId()).isEqualTo(guideProduct.getId());
        assertThat(createdReview.getImages().size()).isEqualTo(images.size());
    }


}