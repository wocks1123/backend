package com.swygbro.trip.backend.domain.reservation.aplication;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.reservation.domain.ReservationRepository;
import com.swygbro.trip.backend.domain.reservation.dto.*;
import com.swygbro.trip.backend.domain.reservation.exception.*;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final IamportClient iamportClient;
    private final ReservationRepository reservationRepository;
    private final GuideProductRepository guideProductRepository;

    /**
     * 예약 정보 저장
     *
     * @param clientId
     * @param reservation
     * @return
     */
    public MerchantDto saveReservation(Long clientId, SaveReservationRequest reservation) {
        try {
            GuideProduct guideProduct = guideProductRepository.findById(reservation.getProductId()).orElseThrow(
                    () -> new GuideProductNotFoundException(reservation.getProductId())
            );
            Reservation entity = reservation.toEntity(clientId, guideProduct.getUser().getId());
            entity.generateMerchantUid();

            if (reservationRepository.findByMerchantUid(entity.getMerchantUid()) != null) {
                throw new DuplicateMerchantUidException(entity.getMerchantUid());
            }

            Reservation save = reservationRepository.save(entity);

            return MerchantDto.builder().
                    merchantUid(save.getMerchantUid()).build();
        } catch (DataIntegrityViolationException e) {
            log.info(e.getMessage());
            throw new ForeignKeyConstraintViolationException("GuideProduct or User");
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    /**
     * 아임포트 서버로부터 결제 정보를 검증
     *
     * @param imp_uid
     */
    public IamportResponse<Payment> validateIamport(String imp_uid) throws IamportResponseException, IOException {
        IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);
        log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse());
        return payment;
    }

    /**
     * 아임포트 서버로부터 결제 취소 요청
     *
     * @param imp_uid
     * @return
     */
    public IamportResponse<Payment> cancelPayment(String imp_uid) throws IamportResponseException, IOException {
        CancelData cancelData = new CancelData(imp_uid, true);
        return iamportClient.cancelPaymentByImpUid(cancelData);
    }

    /**
     * 결제 정보 저장
     *
     * @param request
     * @return
     */
    public ReservationDto savePayment(SavePaymentRequest request) throws IamportResponseException, IOException {
        Reservation reservation = reservationRepository.findByMerchantUid(request.getMerchantUid());

        if (reservation == null) {
            throw new ReservationNotFoundException(request.getMerchantUid());
        }

        if (reservationRepository.findByImpUid(request.getImpUid()) != null) {
            throw new DuplicateImpUidException(request.getImpUid());
        }

        reservation.UpdatePaymentReservation(request);

        reservationRepository.save(reservation);
        return new ReservationDto().fromEntity(reservation);
    }

    /**
     * 예약 취소
     *
     * @param merchant_uid
     * @return
     */
    public ReservationDto cancelReservation(String merchant_uid) throws IamportResponseException, IOException {
        Reservation reservation = reservationRepository.findByMerchantUid(merchant_uid);

        if (reservation == null) {
            throw new ReservationNotFoundException(merchant_uid);
        }

        if ((reservation.getGuideStart().equals(ReservationStatus.CANCELLED)) && (reservation.getPaymentStatus().equals(PayStatus.REFUNDED))) {
            throw new DuplicateCancelReservationException(merchant_uid);
        }

        reservation.cancelReservation();
        IamportResponse<Payment> paymentIamportResponse = cancelPayment(reservation.getImpUid());
        reservation.refundPayment(paymentIamportResponse.getResponse().getCancelledAt());

        reservationRepository.save(reservation);
        return new ReservationDto().fromEntity(reservation);
    }

    /**
     * Client ID 를 통한 예약 조회
     */
    public List<ReservationDto> getReservationListByClient(Long clientId, ReservationSearchCriteria criteria) {
        List<Reservation> reservations = reservationRepository.findReservationsByClientId(clientId, criteria);
        List<ReservationDto> reservationDtos = new ArrayList<>();

        reservations.forEach(reservation -> {
            ReservationDto reservationDto = new ReservationDto().fromEntity(reservation);
            reservationDtos.add(reservationDto);
        });

        return reservationDtos;
    }


    public ReservationDto getReservation(String merchant_uid) {
        Reservation reservation = reservationRepository.findByMerchantUid(merchant_uid);
        if (reservation == null) {
            throw new ReservationNotFoundException(merchant_uid);
        }
        return new ReservationDto().fromEntity(reservation);
    }

    /**
     * Guide ID 를 통한 예약 조회
     */
    public List<ReservationDto> getReservationListByGuide(Long guideId, ReservationSearchCriteria criteria) {
        List<Reservation> reservations = reservationRepository.findReservationsByGuideId(guideId, criteria);
        List<ReservationDto> reservationDtos = new ArrayList<>();

        reservations.forEach(reservation -> {
            ReservationDto reservationDto = new ReservationDto().fromEntity(reservation);
            reservationDtos.add(reservationDto);
        });

        return reservationDtos;
    }


    public ReservationDto confirmReservation(String merchantUid) {
        Reservation reservation = reservationRepository.findByMerchantUid(merchantUid);
        if (reservation == null) {
            throw new ReservationNotFoundException(merchantUid);
        }
        reservation.confirmReservation();
        reservationRepository.save(reservation);
        return new ReservationDto().fromEntity(reservation);
    }

    @Transactional(readOnly = true)
    public Page<ReservationInfoDto> getReservationPages(Long userId, Boolean isGuide, Pageable pageable) {
        var reservations = reservationRepository.findAll(pageable);

        if (userId != null && isGuide) {
            reservations = reservationRepository.findAllByGuideId(userId, pageable);
        } else if (userId != null) {
            reservations = reservationRepository.findAllByClient_Id(userId, pageable);
        }

        return reservations.map(ReservationInfoDto::fromEntity);
    }

}
