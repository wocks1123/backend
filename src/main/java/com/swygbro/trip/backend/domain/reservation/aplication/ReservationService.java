package com.swygbro.trip.backend.domain.reservation.aplication;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.reservation.domain.ReservationRepository;
import com.swygbro.trip.backend.domain.reservation.dto.ReservationDto;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import com.swygbro.trip.backend.domain.reservation.dto.SaveReservationRequest;
import com.swygbro.trip.backend.domain.reservation.exception.ReservationNotFoundException;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import com.swygbro.trip.backend.global.status.PayStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private final IamportClient iamportClient;
    private final ReservationRepository reservationRepository;

    /**
     * 아임포트 서버로부터 결제 정보를 검증
     * @param imp_uid
     */
    public IamportResponse<Payment> validateIamport(String imp_uid) {

        try {
            IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);
            log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse());
            return payment;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    /**
     * 아임포트 서버로부터 결제 취소 요청
     * @param imp_uid
     * @return
     */
    public IamportResponse<Payment> cancelPayment(String imp_uid) {
        try {
            CancelData cancelData = new CancelData(imp_uid, true);
            return iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    /**
     * 주문 정보 저장
     * @param request
     * @return
     */
    public String updatePaymentReservation(SavePaymentRequest request) {
        try {
            Reservation reservation = reservationRepository.findById(request.getMerchantUid())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            reservation.UpdatePaymentReservation(request);
            reservationRepository.save(reservation);
            return "주문 정보가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            log.info(e.getMessage());
            cancelPayment(request.getImpUid());
            return "주문 정보 저장에 실패했습니다.";
        }
    }

    /**
     * 예약 정보 저장
     * @param reservation
     * @return
     */
    public String saveReservation(SaveReservationRequest reservation) {
        try {
            Reservation entity = reservation.toEntity();
            entity.generateMerchantUid();
            Reservation save = reservationRepository.save(entity);
            return save.getMerchantUid();
        } catch (Exception e) {
            log.info(e.getMessage());
            return "예약 정보 저장에 실패했습니다.";
        }
    }

    /**
     * 예약 취소
     * @param merchant_uid
     * @return
     */
    @Transactional
    public String cancelReservation(String merchant_uid) {
        try {
            Reservation reservation = reservationRepository.findByMerchantUid(merchant_uid);

            if (reservation == null) {
                throw new ReservationNotFoundException(merchant_uid);
            }

            if ((reservation.getReservatedAt().equals(ReservationStatus.CANCELLED)) && (reservation.getPaymentStatus().equals(PayStatus.REFUNDED))) {
                return "이미 취소된 예약입니다.";
            }

            reservation.cancelReservation();
            IamportResponse<Payment> paymentIamportResponse = cancelPayment(reservation.getImpUid());
            reservation.refundPayment();

            reservationRepository.save(reservation);
            return paymentIamportResponse.getMessage();
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    //TODO: LOGIN USER ID 로 교체
    public List<ReservationDto> getReservationList() {
        List<Reservation> byClientId = reservationRepository.findByClientId(1L);


    }

    public ReservationDto getReservation(String merchant_uid) {
        return null;
    }
}
