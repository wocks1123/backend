package com.swygbro.trip.backend.domain.reservation.dto;

import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.user.dto.UserInfoDto;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ReservationInfoDto {
    @Schema(description = "예약 고유 번호", example = "1")
    private Long id;

    @Schema(description = "가이드")
    private UserInfoDto guide;

    @Schema(description = "클라이언트")
    private UserInfoDto client;

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

    @Schema(description = "주문 번호", example = "20240429-ad5eea")
    private String merchantUid;

    public static ReservationInfoDto fromEntity(Reservation reservation) {
        ReservationInfoDto reservationInfoDto = new ReservationInfoDto();
        reservationInfoDto.setId(reservation.getId());
        reservationInfoDto.setGuide(UserInfoDto.fromEntity(reservation.getGuide()));
        reservationInfoDto.setClient(UserInfoDto.fromEntity(reservation.getClient()));
        reservationInfoDto.setProduct(GuideProductDto.fromEntity(reservation.getProduct()));
        reservationInfoDto.setGuideStart(reservation.getGuideStart());
        reservationInfoDto.setGuideEnd(reservation.getGuideEnd());
        reservationInfoDto.setPersonnel(reservation.getPersonnel());
        reservationInfoDto.setMessage(reservation.getMessage());
        reservationInfoDto.setPrice(reservation.getPrice());
        reservationInfoDto.setPaymentStatus(reservation.getPaymentStatus());
        reservationInfoDto.setReservationStatus(reservation.getReservationStatus());
        reservationInfoDto.setMerchantUid(reservation.getMerchantUid());
        return reservationInfoDto;
    }
}
