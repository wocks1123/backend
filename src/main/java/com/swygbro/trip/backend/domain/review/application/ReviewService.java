package com.swygbro.trip.backend.domain.review.application;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.reservation.domain.ReservationRepository;
import com.swygbro.trip.backend.domain.review.domain.Review;
import com.swygbro.trip.backend.domain.review.domain.ReviewImage;
import com.swygbro.trip.backend.domain.review.domain.ReviewRepository;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.review.dto.ReviewDetailDto;
import com.swygbro.trip.backend.domain.review.dto.ReviewInfoDto;
import com.swygbro.trip.backend.domain.review.exception.InvalidReviewRequestException;
import com.swygbro.trip.backend.domain.review.exception.ReviewNotFoundException;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final GuideProductRepository guideProductRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public ReviewDetailDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);
        return ReviewDetailDto.builder()
                .reviewId(review.getId())
                .reviewer(review.getReviewer().getNickname())
                .guideProductId(review.getGuideProduct().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .images(review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    @Transactional
    public ReviewInfoDto createReview(CreateReviewRequest dto, User reviewer, List<MultipartFile> images) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(InvalidReviewRequestException::new);

//        // 예약 내역과 요청한 사용자가 일치해야한다.
//        if (!reservation.getClient().equals(reviewer)) {
//            throw new InvalidReviewRequestException();
//        }
//
//        // 예약 상태가 예약완료인 경우에만 리뷰를 작성할 수 있다.
//        if (reservation.getReservationStatus() != ReservationStatus.RESERVED) {
//            throw new InvalidReviewRequestException();
//        }

        Review review = new Review(dto, reviewer, reservation.getProduct());

        if (images != null) {
            images.forEach(image -> {
                review.addReviewImage(new ReviewImage(s3Service.uploadImage(image), review));
            });
        }

        Review createdReview = reviewRepository.save(review);
        return ReviewInfoDto.builder()
                .reviewId(createdReview.getId())
                .reviewer(createdReview.getReviewer().getNickname())
                .guideProductId(createdReview.getGuideProduct().getId())
                .content(createdReview.getContent())
                .rating(createdReview.getRating())
                .createdAt(createdReview.getCreatedAt())
                .build();
    }

}
