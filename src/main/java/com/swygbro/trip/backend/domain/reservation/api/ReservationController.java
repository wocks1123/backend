package com.swygbro.trip.backend.domain.reservation.api;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.swygbro.trip.backend.domain.reservation.aplication.ReservationService;
import com.swygbro.trip.backend.domain.reservation.dto.ReservationDto;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import com.swygbro.trip.backend.domain.reservation.dto.SaveReservationRequest;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservation", description = "결제 API")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/client/save")
    @Operation(summary = "예약 정보 저장", description = "예약 정보를 저장합니다.", tags = "Client")
    @ApiResponse(
            responseCode = "200",
            description = "예약 정보 저장 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(
            responseCode = "500",
            description = "예약 정보 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))
    )
    public ResponseEntity<String> saveReservation(@RequestBody SaveReservationRequest orderDto) {
        try {
            log.info("Received orders: {}", orderDto.toString());
            return ResponseEntity.ok(reservationService.saveReservation(orderDto));
        } catch (Exception e) {
            log.info("Failed to receive orders: {}", orderDto.toString());
            return ResponseEntity.badRequest().body("예약 정보 저장에 실패했습니다.");
        }
    }

    @PostMapping("/client/{imp_uid}")
    @Operation(summary = "결제 검증", description = "아임포트 서버로부터 결제 정보를 검증합니다.", tags = "Client")
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
        return reservationService.validateIamport(imp_uid);
    }

    @PostMapping("/client/payment")
    @Operation(summary = "결제 정보 저장", description = "결제 정보를 저장합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "결제 정보 저장 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(
            responseCode = "500",
            description = "결제 정보 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))
    )
    public ResponseEntity<String> processOrder(@RequestBody SavePaymentRequest orderDto) {
        try {
            log.info("Received orders: {}", orderDto.toString());
            return ResponseEntity.ok(reservationService.savePayment(orderDto));
        } catch (Exception e) {
            log.info("Failed to receive orders: {}", orderDto.toString());
            return ResponseEntity.badRequest().body("결제 정보 저장에 실패했습니다.");
        }
    }


    @PostMapping("/client/cancel/{merchant_uid}")
    @Operation(summary = "예약 취소", description = "예약 및 결제 취소 요청합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "예약 및 결제 취소 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> cancelPayment(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.cancelReservation(merchant_uid));
    }

    @GetMapping("client/list")
    public ResponseEntity<List<ReservationDto>> getReservationList() {
        return ResponseEntity.ok(reservationService.getReservationList());
    }

    @GetMapping("client/{merchant_uid}")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.getReservation(merchant_uid));
    }
}


