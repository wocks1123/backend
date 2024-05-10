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

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;


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
    private ZonedDateTime reservatedAt;

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

    @Column(unique = true)
    private String merchantUid;

    @Column(unique = true)
    private String impUid;

    private Long paidAt;

    private Long cancelledAt;

    public void UpdatePaymentReservation(SavePaymentRequest savePaymentRequest) {
        this.impUid = savePaymentRequest.getImpUid();
        this.paidAt = savePaymentRequest.getPaidAt();
        this.paymentStatus = PayStatus.COMPLETE;
    }

    public void setClientId(Long clientId) {
        this.client = User.builder().id(clientId).build();
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

    public void refundPayment(Date cancelledAt) {
        this.paymentStatus = PayStatus.REFUNDED;
        this.cancelledAt = cancelledAt.getTime();
    }

    public void generateMerchantUid() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-");
        String prefix = dateFormat.format(new Date());

        UUID uuid = UUID.randomUUID();
        String suffix = uuid.toString().substring(0, 6);

        this.merchantUid = prefix + suffix;
    }


}