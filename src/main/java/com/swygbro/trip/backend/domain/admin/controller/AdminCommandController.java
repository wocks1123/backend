package com.swygbro.trip.backend.domain.admin.controller;


import com.siot.IamportRestClient.exception.IamportResponseException;
import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.reservation.aplication.ReservationService;
import com.swygbro.trip.backend.domain.reservation.dto.SaveReservationRequest;
import com.swygbro.trip.backend.domain.review.application.ReviewService;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.review.dto.UpdateReviewRequest;
import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCommandController {

    private final UserService userService;
    private final GuideProductService guideProductService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    @PutMapping(value = "/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateUserByAdmin(@PathVariable Long userId,
                                  @RequestPart @Valid UpdateUserRequest dto,
                                  @RequestPart(required = false) MultipartFile imageFile) {
        userService.updateUser(userId, dto, imageFile);
    }

    @PostMapping(value = "/guideProducts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createGuideProduct(String nickname,
                                   @Valid @RequestPart CreateGuideProductRequest request,
                                   @RequestPart(value = "thumb") MultipartFile thumbImage,
                                   @RequestPart(value = "file", required = false) Optional<List<MultipartFile>> images) {
        User user = userService.findByNickname(nickname);

        guideProductService.createGuideProduct(user, request, thumbImage, images);
    }

    @PutMapping(value = "/guideProducts/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateGuideProduct(String nickname,
                                   @PathVariable Long productId,
                                   @Valid @RequestPart ModifyGuideProductRequest request,
                                   @RequestPart(value = "thumb", required = false) Optional<MultipartFile> modifyThumbImage,
                                   @RequestPart(value = "file", required = false) Optional<List<MultipartFile>> modifyImages) {
        User user = userService.findByNickname(nickname);
        guideProductService.modifyGuideProduct(user, productId, request, modifyThumbImage, modifyImages);
    }

    @DeleteMapping("/guideProducts/{productId}")
    public void deleteGuideProduct(String nickname, @PathVariable Long productId) {
        User user = userService.findByNickname(nickname);
        guideProductService.deleteGuideProduct(productId, user);
    }

    @PostMapping("/reservations/confirm/{merchant_uid}")
    public void confirmReservation(@PathVariable String merchant_uid) {
        reservationService.confirmReservation(merchant_uid);
    }

    @PostMapping("/reservations/cancel/{merchant_uid}")
    public void cancelReservation(@PathVariable String merchant_uid) throws IamportResponseException, IOException {
        reservationService.cancelReservation(merchant_uid);
    }

    @PostMapping("/reservations/save")
    public void saveReservation(String nickname,
                                @RequestBody @Valid SaveReservationRequest dto) {
        User user = userService.findByNickname(nickname);
        reservationService.saveReservation(user.getId(), dto);
    }

    @PostMapping(value = "/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createReview(@RequestPart CreateReviewRequest dto,
                             @RequestPart(value = "file", required = false) List<MultipartFile> images,
                             String nickname) {
        User reviewer = userService.findByNickname(nickname);

        reviewService.createReview(dto, reviewer, images);
    }

    @PutMapping(value = "/reviews/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateReview(@PathVariable Long reviewId,
                             @RequestPart UpdateReviewRequest request,
                             @RequestPart(value = "images", required = false) Optional<List<MultipartFile>> images) {
        reviewService.updateReview(reviewId, request, images);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

}
