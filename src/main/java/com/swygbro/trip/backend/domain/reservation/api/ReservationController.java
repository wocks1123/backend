package com.swygbro.trip.backend.domain.reservation.api;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.swygbro.trip.backend.domain.reservation.aplication.ReservationService;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "결제 API")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/validation/{imp_uid}")
    @Operation(summary = "결제 검증", description = "아임포트 서버로부터 결제 정보를 검증합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "결제 정보 검증 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IamportResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "결제 정보 검증 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IamportResponse.class)))
    public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        log.info("imp_uid: {}", imp_uid);
        log.info("validateIamport");
        return reservationService.validateIamport(imp_uid);
    }

    @PostMapping("/order")
    @Operation(summary = "주문 정보 저장", description = "주문 정보를 저장합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "주문 정보 저장 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(
            responseCode = "500",
            description = "주문 정보 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))
    )
    public ResponseEntity<String> processOrder(@RequestBody SavePaymentRequest orderDto) {
        try {
            // 주문 정보를 로그에 출력
            log.info("Received orders: {}", orderDto.toString());
            // 성공적으로 받아들였다는 응답 반환
            return ResponseEntity.ok(reservationService.updatePaymentReservation(orderDto));
        } catch (Exception e) {
            // 주문 정보를 로그에 출력
            log.info("Failed to receive orders: {}", orderDto.toString());
            // 실패했다는 응답 반환
            return ResponseEntity.badRequest().body("주문 정보 저장에 실패했습니다.");
        }
    }


    @PostMapping("/cancel/{imp_uid}")
    @Operation(summary = "결제 취소", description = "아임포트 서버로부터 결제 취소 요청합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "결제 취소 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IamportResponse.class)))
    public IamportResponse<Payment> cancelPayment(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        return reservationService.cancelPayment(imp_uid);
    }
}


