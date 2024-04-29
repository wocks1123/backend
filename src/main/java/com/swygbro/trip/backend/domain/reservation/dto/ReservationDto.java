package com.swygbro.trip.backend.domain.reservation.dto;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.PayStatusConverter;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import com.swygbro.trip.backend.global.status.ReservationStatusConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

//TODO: User, GuideProduct to DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    @Schema(description = "가이드")
    private User guide;

    @Schema(description = "상품")
    private GuideProduct product;

    @Schema(description = "예약 날짜", example = "2024-04-29T12:30:45Z")
    private Timestamp reservatedAt;

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

    @Schema(description = "주문 번호", example = "20240429-ad5eea")
    private String merchantUid;


    public ReservationDto fromEntity(Reservation reservation){
        return new ReservationDto(
                reservation.getGuide(),
                reservation.getProduct(),
                reservation.getReservatedAt(),
                reservation.getPersonnel(),
                reservation.getMessage(),
                reservation.getPrice(),
                reservation.getPaymentStatus(),
                reservation.getReservationStatus(),
                reservation.getMerchantUid()
        );
    }
}
