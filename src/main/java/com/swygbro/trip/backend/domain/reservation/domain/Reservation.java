package com.swygbro.trip.backend.domain.reservation.domain;

import com.swygbro.trip.backend.domain.product.domain.GuideProduct;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @Column(nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    @Column(nullable = false)
    private User guide;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @Column(nullable = false)
    private GuideProduct productId;

    @Column(nullable = false)
    private Timestamp reservatedAt;

    @Column(nullable = false)
    private Integer personnel;

    private String message;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private PayStatus paymentStatus;

    @Column(nullable = false)
    private ReservationStatus reservationStatus;

    private String impUid;

    private Timestamp paidAt;

    public void UpdatePaymentReservation(SavePaymentRequest savePaymentRequest) {
        this.impUid = savePaymentRequest.getImpUid();
        this.paidAt = savePaymentRequest.getPaidAt();
        this.paymentStatus = PayStatus.COMPLETE;
    }


}