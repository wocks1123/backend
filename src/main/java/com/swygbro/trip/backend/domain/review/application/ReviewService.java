package com.swygbro.trip.backend.domain.review.application;

import com.swygbro.trip.backend.domain.alarm.application.AlarmService;
import com.swygbro.trip.backend.domain.alarm.domain.Alarm;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmArgs;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmRepository;
import com.swygbro.trip.backend.domain.alarm.domain.AlarmType;
import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.reservation.domain.ReservationRepository;
import com.swygbro.trip.backend.domain.review.domain.Review;
import com.swygbro.trip.backend.domain.review.domain.ReviewImage;
import com.swygbro.trip.backend.domain.review.domain.ReviewRepository;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.review.dto.ReviewDetailDto;
import com.swygbro.trip.backend.domain.review.dto.ReviewInfoDto;
import com.swygbro.trip.backend.domain.review.dto.UpdateReviewRequest;
import com.swygbro.trip.backend.domain.review.exception.InvalidReviewRequestException;
import com.swygbro.trip.backend.domain.review.exception.ReviewNotFoundException;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final GuideProductRepository guideProductRepository;
    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;
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

        Review review = new Review(dto, reviewer, reservation);

        if (images != null) {
            images.forEach(image -> {
                review.addReviewImage(new ReviewImage(s3Service.uploadImage(image)));
            });
        }

        Review createdReview = reviewRepository.save(review);

        // 가이드에게 리뷰 작성 알림 발송
        Alarm alarm = alarmRepository.save(Alarm.of(reservation.getGuide(),
                AlarmType.NEW_REVIEW,
                new AlarmArgs(reservation.getClient().getId(),
                        reservation.getProduct().getId()),
                false));
        alarmService.send(alarm.getId(), reservation.getGuide().getId(), AlarmDto.fromEntity(alarm));

        return ReviewInfoDto.builder()
                .reviewId(createdReview.getId())
                .reviewer(createdReview.getReviewer().getNickname())
                .guideProductId(createdReview.getGuideProduct().getId())
                .content(createdReview.getContent())
                .rating(createdReview.getRating())
                .createdAt(createdReview.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<ReviewInfoDto> getReviewPagesByGuideId(Pageable pageable, Long guideProductId, Long userId) {
        if (guideProductId == null) {
            return findAllReviewPages(pageable);
        }
        return findReviewPagesByGuideId(pageable, guideProductId);
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequest request, Optional<List<MultipartFile>> imageFiles) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
        var notIncluded = review.getImages().stream()
                .filter(image -> !request.getImages().contains(image.getImageUrl()))
                .toList();

        notIncluded.forEach(image -> {
            s3Service.deleteImage(image.getImageUrl());
        });
        review.getImages().removeIf(notIncluded::contains);

        imageFiles.ifPresent(images -> {
            images.forEach(img -> {
                review.addReviewImage(new ReviewImage(s3Service.uploadImage(img)));
            });
        });

        review.update(request);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
        reviewRepository.delete(review);
    }


    private Page<ReviewInfoDto> findAllReviewPages(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(this::convertToReviewInfoDto);
    }

    private Page<ReviewInfoDto> findReviewPagesByGuideId(Pageable pageable, Long guideProductId) {
        return reviewRepository.findAllByGuideProductId(guideProductId, pageable)
                .map(this::convertToReviewInfoDto);
    }

    private ReviewInfoDto convertToReviewInfoDto(Review review) {
        return ReviewInfoDto.builder()
                .reviewId(review.getId())
                .reviewer(review.getReviewer().getNickname())
                .guideProductId(review.getGuideProduct().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
