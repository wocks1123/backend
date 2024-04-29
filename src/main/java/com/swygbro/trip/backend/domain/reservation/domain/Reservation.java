package com.swygbro.trip.backend.domain.reservation.domain;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.PayStatusConverter;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import com.swygbro.trip.backend.global.status.ReservationStatusConverter;
import jakarta.persistence.*;
import lombok.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private User guide;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private GuideProduct product;

    @Column(nullable = false)
    private Timestamp reservatedAt;

    @Column(nullable = false)
    private Integer personnel;

    private String message;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Convert(converter = PayStatusConverter.class)
    private PayStatus paymentStatus;

    @Column(nullable = false)
    @Convert(converter = ReservationStatusConverter.class)
    private ReservationStatus reservationStatus;

    private String merchantUid;

    private String impUid;

    private Timestamp paidAt;

    public void UpdatePaymentReservation(SavePaymentRequest savePaymentRequest) {
        this.impUid = savePaymentRequest.getImpUid();
        this.paidAt = new Timestamp(savePaymentRequest.getPaidAt() * 1000);
        this.paymentStatus = PayStatus.COMPLETE;
    }

    public void cancelReservation() {
        this.reservationStatus = ReservationStatus.CANCELLED;
    }

    public void confirmReservation() {
        this.reservationStatus = ReservationStatus.RESERVED;
    }

    public void settleReservation() {
        this.reservationStatus = ReservationStatus.SETTLED;
    }

    public void refundPayment() {
        this.paymentStatus = PayStatus.REFUNDED;
    }

    public void generateMerchantUid() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss-");
        String prefix = dateFormat.format(new Date());

        try {
            String number = this.client.getId().toString() + this.product.getId();
            // 숫자를 바이트 배열로 변환하여 해시 함수에 전달
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(number.getBytes());

            // 해시를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            this.merchantUid = prefix + hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.merchantUid = null;
        }
    }

}