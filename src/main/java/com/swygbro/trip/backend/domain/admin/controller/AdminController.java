package com.swygbro.trip.backend.domain.admin.controller;

import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.reservation.aplication.ReservationService;
import com.swygbro.trip.backend.domain.reservation.dto.ReservationInfoDto;
import com.swygbro.trip.backend.domain.review.application.ReviewService;
import com.swygbro.trip.backend.domain.review.dto.ReviewInfoDto;
import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final GuideProductService guideProductService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    @GetMapping("/users")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int offset,
                               @RequestParam(defaultValue = "10") int limit,
                               @RequestParam(defaultValue = "id") String sort) {
        return userService.getUserPages(PageRequest.of(offset, limit, Sort.by(sort)));
    }

    @GetMapping("guideProducts")
    public Page<GuideProductDto> getGuideProducts(@RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "10") int limit,
                                                  @RequestParam(defaultValue = "id") String sort) {
        return guideProductService.getGuideProductPages(PageRequest.of(offset, limit, Sort.by(sort)));
    }

    @GetMapping("reservations")
    public Page<ReservationInfoDto> getReservations(@RequestParam(defaultValue = "0") int offset,
                                                    @RequestParam(defaultValue = "10") int limit,
                                                    @RequestParam(defaultValue = "id") String sort,
                                                    @RequestParam(required = false) Long userId,
                                                    @RequestParam(required = false) Boolean isGuide) {
        return reservationService.getReservationPages(userId, isGuide, PageRequest.of(offset, limit, Sort.by(sort)));
    }

    @GetMapping("review")
    public Page<ReviewInfoDto> getReviews(@RequestParam(defaultValue = "0") int offset,
                                          @RequestParam(defaultValue = "10") int limit,
                                          @RequestParam(defaultValue = "id") String sort,
                                          @RequestParam(required = false) Long guideProductId,
                                          @RequestParam(required = false) Long userId) {
        return reviewService.getReviewPagesByGuideId(PageRequest.of(offset, limit, Sort.by(sort)), guideProductId, userId);
    }

}
