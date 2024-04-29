package com.swygbro.trip.backend.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class SavePaymentRequest {
    @Schema(description = "상품 ID", example = "1")
    String merchantUid;

    @Schema(description = "결제 상품 ID", example = "1")
    Long productId;

    @Schema(description = "결제 고유 번호", example = "imp_1234567890")
    String impUid;

    @Schema(description = "결제 시간 (UNIX 타임스탬프)", example = "1648344363")
    Long paidAt;

    @Schema(description = "결제 금액", example = "10000")
    int price;

    @Schema(description = "결제 수량", example = "1")
    int quantity;
}
