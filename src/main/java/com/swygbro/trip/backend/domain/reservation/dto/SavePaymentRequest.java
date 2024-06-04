package com.swygbro.trip.backend.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavePaymentRequest {
    @NotNull
    @Schema(description = "상품 ID", example = "20240523-be091be1")
    String merchantUid;

    @NotNull
    @Schema(description = "결제 상품 ID", example = "11")
    Long productId;

    @NotNull
    @Schema(description = "결제 고유 번호", example = "imp_1234567890")
    String impUid;

    @NotNull
    @Schema(description = "결제 시간 (UNIX 타임스탬프)", example = "1648344363")
    Long paidAt;

    @NotNull
    @Schema(description = "결제 금액", example = "10000")
    int price;

    @NotNull
    @Min(value = 1, message = "결제 수량은 1 이상이어야 합니다.")
    @Schema(description = "결제 수량", example = "1")
    int personnel;
}
