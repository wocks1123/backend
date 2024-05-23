package com.swygbro.trip.backend.domain.reservation.dto;

import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.user.dto.SimpleUserDto;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    @Schema(description = "가이드")
    private SimpleUserDto guide;

    @Schema(description = "상품")
    private GuideProductDto product;

    @Schema(description = "가이드 시작 날짜", example = "2024-04-29T12:30:45Z")
    private ZonedDateTime guideStart;

    @Schema(description = "가이드 종료 날짜", example = "2024-04-30T12:30:45Z")
    private ZonedDateTime guideEnd;

    @Schema(description = "인원", example = "1")
    private Integer personnel;

    @Schema(description = "메시지", example = "안녕하세요")
    private String message;

    @Schema(description = "가격", example = "10000")
    private Integer price;

    @Schema(description = "결제 상태", example = "COMPLETE")
    private PayStatus paymentStatus;

    @Schema(description = "예약 상태", example = "RESERVED")
    private ReservationStatus reservationStatus;

    @Schema(description = "주문 번호", example = "20240523-be091be1")
    private String merchantUid;


    public ReservationDto fromEntity(Reservation reservation) {
        return ReservationDto.builder()
                .guide(SimpleUserDto.fromEntity(reservation.getGuide()))
                .product(GuideProductDto.fromEntity(reservation.getProduct()))
                .guideStart(reservation.getGuideStart())
                .guideEnd(reservation.getGuideEnd())
                .personnel(reservation.getPersonnel())
                .message(reservation.getMessage())
                .price(reservation.getPrice())
                .paymentStatus(reservation.getPaymentStatus())
                .reservationStatus(reservation.getReservationStatus())
                .merchantUid(reservation.getMerchantUid())
                .build();
    }
}
