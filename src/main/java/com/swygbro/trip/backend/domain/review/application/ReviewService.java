package com.swygbro.trip.backend.domain.review.application;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.review.domain.Review;
import com.swygbro.trip.backend.domain.review.domain.ReviewImage;
import com.swygbro.trip.backend.domain.review.domain.ReviewRepository;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
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
    private final GuideProductRepository guideProductRepository;
    private final S3Service s3Service;


    @Transactional
    public Long createReview(CreateReviewRequest dto, User reviewer, List<MultipartFile> images) {
        GuideProduct product = guideProductRepository.findById(dto.getGuideProductId())
                .orElseThrow(() -> new GuideProductNotFoundException(dto.getGuideProductId()));

        Review review = new Review(dto, reviewer, product);
        images.forEach(image -> {
            review.addReviewImage(new ReviewImage(s3Service.uploadImage(image)));
        });

        Review createdReview = reviewRepository.save(review);
        return createdReview.getId();
    }

}
