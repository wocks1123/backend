package com.swygbro.trip.backend.domain.reservation.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SavePaymentRequest {
    Long merchantUid;
    String impUid;
    Timestamp paidAt;
    Long productId;
    int price;
    int quantity;
}
