package com.swygbro.trip.backend.domain.reservation.aplication;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.reservation.domain.ReservationRepository;
import com.swygbro.trip.backend.domain.reservation.dto.ReservationDto;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import com.swygbro.trip.backend.domain.reservation.dto.SaveReservationRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@Transactional
@Sql(scripts = {"/user.sql", "/guideProduct.sql"})
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;


    @Test
    @DisplayName("예약 정보 저장")
    void saveReservation() {
        // given
        SaveReservationRequest saveReservationRequest = SaveReservationRequest.builder()
                .guideId(1L)
                .productId(1)
                .reservatedAt(Timestamp.valueOf("2024-04-29 12:30:45"))
                .personnel(1)
                .message("안녕하세요")
                .price(10000)
                .build();

        // when
        String result = reservationService.saveReservation(saveReservationRequest);

        // then
        Reservation reservation = reservationRepository.findByMerchantUid(result);
        log.info("reservation merchantUid: {}", reservation.getMerchantUid());

        assertThat(reservation.getGuide().getId()).isEqualTo(saveReservationRequest.getGuideId());
        assertThat(reservation.getProduct().getId()).isEqualTo(saveReservationRequest.getProductId());
        assertThat(result).isEqualTo(reservation.getMerchantUid());

    }

    @Test
    @DisplayName("예약 정보 저장 실패")
    void saveReservationFail() {
    }

    @Test
    @DisplayName("결제 정보 저장")
    void savePayment() {
        // given
        SaveReservationRequest saveReservationRequest = SaveReservationRequest.builder()
                .guideId(1L)
                .productId(1)
                .reservatedAt(Timestamp.valueOf("2024-04-29 12:30:45"))
                .personnel(1)
                .message("안녕하세요")
                .price(10000)
                .build();

        String merchantUid = reservationService.saveReservation(saveReservationRequest);

        SavePaymentRequest savePaymentRequest = SavePaymentRequest.builder()
                .impUid("imp_1234567890")
                .merchantUid(merchantUid)
                .price(10000)
                .quantity(1)
                .paidAt(1648344363L)
                .build();

        // when
        String result = reservationService.savePayment(savePaymentRequest);

        // then
        Reservation reservation = reservationRepository.findByMerchantUid(merchantUid);

        log.info("result : {}", result);
        log.info("reservation : {}", reservation);
        log.info("reservation paidAt : {}", reservation.getPaidAt());

        assertThat(reservation.getMerchantUid()).isEqualTo(merchantUid);
        assertThat(reservation.getPaymentStatus().toString()).isEqualTo("COMPLETE");
        assertThat(reservation.getPaidAt()).isInstanceOf(Timestamp.class);
    }

    @Test
    @DisplayName("결제 후 예약 취소 (실제 uid 필요)")
    void cancelReservation() {
        // given

        // 실제 PortOne test merchantUid
        String merchantUid = "21A1021AB123AAC";
        String impUid = "imp_977081467885";

        reservationRepository.save(Reservation.builder()
                .client(User.builder().id(1L).build())
                .guide(User.builder().id(2L).build())
                .product(GuideProduct.builder().id(1).build())
                .reservatedAt(Timestamp.valueOf("2024-04-29 12:30:45"))
                .personnel(1)
                .message("안녕하세요")
                .price(10000)
                .paymentStatus(PayStatus.COMPLETE)
                .reservationStatus(ReservationStatus.RESERVED)
                .merchantUid(merchantUid)
                .impUid(impUid)
                .build());

        // when
        String result = reservationService.cancelReservation(merchantUid);
        Reservation reservation = reservationRepository.findByMerchantUid(merchantUid);

        // then
        assertThat(result).isEqualTo("취소 및 환불이 완료되었습니다.");
        assertThat(reservation.getReservationStatus().toString()).isEqualTo("CANCELLED");
        assertThat(reservation.getPaymentStatus().toString()).isEqualTo("REFUNDED");
    }

    @Test
    @DisplayName("결제 전 예약 취소")
    void cancelReservationBeforePayment() {
        // given


    }

    @Test
    @DisplayName("예약 리스트 조회")
    void getReservationList() {
        // given
        reservationRepository.save(Reservation.builder()
                .client(User.builder().id(1L).build())
                .guide(User.builder().id(2L).build())
                .product(GuideProduct.builder().id(1).build())
                .reservatedAt(Timestamp.valueOf("2024-04-29 12:30:45"))
                .personnel(1)
                .message("안녕하세요")
                .price(10000)
                .paymentStatus(PayStatus.COMPLETE)
                .reservationStatus(ReservationStatus.RESERVED)
                .merchantUid("merchant_uid_2")
                .impUid("imp_uid_2")
                .build());

        reservationRepository.save(Reservation.builder()
                .client(User.builder().id(1L).build())
                .guide(User.builder().id(2L).build())
                .product(GuideProduct.builder().id(2).build())
                .reservatedAt(Timestamp.valueOf("2024-04-28 12:30:45"))
                .personnel(1)
                .message("두 번째 예약")
                .price(15000)
                .paymentStatus(PayStadtus.COMPLETE)
                .reservationStatus(ReservationStatus.RESERVED)
                .merchantUid("merchant_uid_3")
                .impUid("imp_uid_3")
                .build());

        // when
        List<ReservationDto> reservationList = reservationService.getReservationList();

        // then
        assertThat(reservationList.size()).isEqualTo(2);
        assertThat(reservationList.get(0).getMerchantUid()).isEqualTo("merchant_uid_2");
        assertThat(reservationList.get(1).getMerchantUid()).isEqualTo("merchant_uid_3");
    }

    @Test
    @DisplayName("예약 단건 조회")
    void getReservation() {
        // given
        reservationRepository.save(Reservation.builder()
                .client(User.builder().id(1L).build())
                .guide(User.builder().id(2L).build())
                .product(GuideProduct.builder().id(1).build())
                .reservatedAt(Timestamp.valueOf("2024-04-29 12:30:45"))
                .personnel(1)
                .message("안녕하세요")
                .price(10000)
                .paymentStatus(PayStatus.COMPLETE)
                .reservationStatus(ReservationStatus.RESERVED)
                .merchantUid("merchant_uid_4")
                .impUid("imp_uid_4")
                .build());


        // when
        ReservationDto reservationDto = reservationService.getReservation("merchant_uid_4");

        // then
        assertThat(reservationDto.getMerchantUid()).isEqualTo("merchant_uid_4");
        assertThat(reservationDto.getPrice()).isEqualTo(10000);
        assertThat(reservationDto.getReservatedAt()).isEqualTo(Timestamp.valueOf("2024-04-29 12:30:45"));
        assertThat(reservationDto.getPersonnel()).isEqualTo(1);
        assertThat(reservationDto.getMessage()).isEqualTo("안녕하세요");

    }


}