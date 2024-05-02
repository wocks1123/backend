package com.swygbro.trip.backend.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveReservationRequest {
    @NotNull
    @Schema(description = "가이드 ID", example = "1")
    Long guideId;

    @NotNull
    @Schema(description = "상품 ID", example = "1")
    Long productId;

    @NotNull
    @Schema(description = "예약 날짜", example = "2024-05-01 12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    ZonedDateTime reservatedAt;

    @NotNull
    @Min(value = 1, message = "인원은 1 이상이어야 합니다.")
    @Schema(description = "인원", example = "1")
    Integer personnel;

    @Schema(description = "메시지", example = "안녕하세요")
    String message;

    @NotNull
    @Schema(description = "가격", example = "10000")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    Integer price;


    public Reservation toEntity() {
        return Reservation.builder()
                .guide(User.builder().id(guideId).build())
                .product(GuideProduct.builder().id(productId).build())
                .reservatedAt(reservatedAt != null ? reservatedAt : null)
                .personnel(personnel != null ? personnel : null)
                .message(message != null ? message : null)
                .price(price != null ? price : null)
                .paymentStatus(PayStatus.PENDING)
                .reservationStatus(ReservationStatus.PENDING_CONFIRMATION)
                .build();
    }
}