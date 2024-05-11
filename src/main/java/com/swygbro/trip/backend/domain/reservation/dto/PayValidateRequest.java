package com.swygbro.trip.backend.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayValidateRequest {
    @NotNull
    private String imp_uid;
}
