package com.swygbro.trip.backend.domain.admin.controller;

import com.swygbro.trip.backend.domain.admin.dao.GuideProductDao;
import com.swygbro.trip.backend.domain.admin.dao.ReservationDao;
import com.swygbro.trip.backend.domain.admin.dao.ReviewDao;
import com.swygbro.trip.backend.domain.admin.dto.GuideProductDetailDto;
import com.swygbro.trip.backend.domain.admin.dto.ReservationDetailDto;
import com.swygbro.trip.backend.domain.admin.dto.ReviewDetailDto;
import com.swygbro.trip.backend.domain.admin.dto.UserInfoCard;
import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.reservation.aplication.ReservationService;
import com.swygbro.trip.backend.domain.reservation.dto.ReservationDto;
import com.swygbro.trip.backend.domain.review.application.ReviewService;
import com.swygbro.trip.backend.domain.user.application.UserService;
import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.domain.SignUpType;
import com.swygbro.trip.backend.domain.user.domain.UserDao;
import com.swygbro.trip.backend.domain.user.dto.UserDetailDto;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminQueryController {

    private final UserService userService;
    private final GuideProductService guideProductService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    private final UserDao userDao;
    private final GuideProductDao guideProductDao;
    private final ReservationDao reservationDao;
    private final ReviewDao reviewDao;


    @GetMapping("/users")
    public Page<UserDetailDto> getUsersDetails(@RequestParam(defaultValue = "0") int offset,
                                               @RequestParam(defaultValue = "10") int limit,
                                               @RequestParam(defaultValue = "id") String sort,
                                               @RequestParam(required = false) String email,
                                               @RequestParam(required = false) String nickname,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(required = false) String phone,
                                               @RequestParam(required = false) String location,
                                               @RequestParam(required = false) Nationality nationality,
                                               @RequestParam(required = false) LocalDate birthdate,
                                               @RequestParam(required = false) Gender gender,
                                               @RequestParam(required = false) SignUpType signUpType) {

        return userService.getUsersByFilter(PageRequest.of(offset, limit, Sort.by(sort)), email,
                nickname,
                name,
                phone,
                location,
                nationality,
                birthdate,
                gender,
                signUpType);
    }

    @GetMapping("/users/{userId}")
    public UserInfoCard getUserInfoCard(@PathVariable Long userId) {
        return userDao.getUserInfoCard(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @GetMapping("/guideProducts")
    public Page<GuideProductDetailDto> getGuideProductsByFilter(@RequestParam(defaultValue = "0") int offset,
                                                                @RequestParam(defaultValue = "10") int limit,
                                                                @RequestParam(defaultValue = "id") String sort,
                                                                @RequestParam(required = false) Long id,
                                                                @RequestParam(required = false) String title,
                                                                @RequestParam(required = false) String nickname,
                                                                @RequestParam(required = false) String locationName) {
        return guideProductDao.findGuideProductsByFilter(PageRequest.of(offset, limit, Sort.by(sort)), id, title, nickname, locationName);
    }

    @GetMapping("/guideProducts/{productId}")
    public GuideProductDto getGuideProductDetail(@PathVariable Long productId) {
        return guideProductService.getProduct(productId);
    }

    @GetMapping("/reservations")
    public Page<ReservationDetailDto> getReservationsByFilter(@RequestParam(required = false, defaultValue = "0") int offset,
                                                              @RequestParam(required = false, defaultValue = "10") int limit,
                                                              @RequestParam(required = false, defaultValue = "id") String sort,
                                                              @RequestParam(required = false) Long id,
                                                              @RequestParam(required = false) String merchantUid,
                                                              @RequestParam(required = false) Long productId,
                                                              @RequestParam(required = false) String client,
                                                              @RequestParam(required = false) String guide,
                                                              @RequestParam(required = false) PayStatus payStatus,
                                                              @RequestParam(required = false) ReservationStatus reservationStatus) {
        return reservationDao.findReservationsByFilter(PageRequest.of(offset, limit, Sort.by(sort)), id, merchantUid, productId, client, guide, payStatus, reservationStatus);
    }

    @GetMapping("/reservations/{merchantUid}")
    public ReservationDto getReservationDetail(@PathVariable String merchantUid) {
        return reservationService.getReservation(merchantUid);
    }

    @GetMapping("/reviews")
    public Page<ReviewDetailDto> getReviewsByFilter(@RequestParam(defaultValue = "0") int offset,
                                                    @RequestParam(defaultValue = "10") int limit,
                                                    @RequestParam(defaultValue = "id") String sort,
                                                    @RequestParam(required = false) Long id,
                                                    @RequestParam(required = false) String reviewer,
                                                    @RequestParam(required = false) Long guideProductId) {
        return reviewDao.findReviewsByFilter(PageRequest.of(offset, limit, Sort.by(sort)), id, reviewer, guideProductId);
    }

    @GetMapping("/reviews/{reviewId}")
    public com.swygbro.trip.backend.domain.review.dto.ReviewDetailDto getReviewDetail(@PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

}
