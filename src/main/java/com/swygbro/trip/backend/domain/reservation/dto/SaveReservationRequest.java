package com.swygbro.trip.backend.domain.reservation.dto;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public class SaveReservationRequest {
    @NotNull
    @Schema(description = "가이드 ID", example = "1")
    Long guideId;

    @NotNull
    @Schema(description = "상품 ID", example = "1")
    int productId;

    @Schema(description = "예약 날짜", example = "2024-04-29T12:30:45Z")
    Timestamp reservatedAt;

    @Schema(description = "인원", example = "1")
    Integer personnel;

    @Schema(description = "메시지", example = "안녕하세요")
    String message;

    @Schema(description = "가격", example = "10000")
    Integer price;


    public Reservation toEntity() {
        return Reservation.builder()
                .guide(User.builder().id(guideId).build())
                .productId(GuideProduct.builder().id(productId).build())
                .reservatedAt(reservatedAt != null ? reservatedAt : null)
                .personnel(personnel != null ? personnel : null)
                .message(message != null ? message : null)
                .price(price != null ? price : null)
                .build();
    }
}
