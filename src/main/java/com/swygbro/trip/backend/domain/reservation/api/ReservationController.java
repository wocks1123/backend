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
import jakarta.validation.Valid;
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
            responseCode = "400",
            description = "예약 정보 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(
            responseCode = "409",
            description = "외래 키 참조 에러",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> saveReservation(@RequestBody @Valid SaveReservationRequest orderDto) {
        log.info("Received orders: {}", orderDto.toString());
        return ResponseEntity.ok(reservationService.saveReservation(orderDto));
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
            responseCode = "404",
            description = "존재하지 않는 결제 정보",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IamportResponse.class)))
    public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        return reservationService.validateIamport(imp_uid);
    }

    @PostMapping("/client/payment")
    @Operation(summary = "결제 정보 저장", description = "결제 정보를 저장합니다.", tags = "Client")
    @ApiResponse(
            responseCode = "200",
            description = "결제 정보 저장 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "결제 정보 저장 실패",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    public ResponseEntity<ReservationDto> processOrder(@RequestBody @Valid SavePaymentRequest orderDto) throws IamportResponseException, IOException {
        log.info("Received orders: {}", orderDto.toString());
        return ResponseEntity.ok(reservationService.savePayment(orderDto));
    }


    @PostMapping("/client/cancel/{merchant_uid}")
    @Operation(summary = "예약 취소", description = "예약 및 결제 취소 요청합니다.", tags = "Client")
    @ApiResponse(
            responseCode = "200",
            description = "예약 및 결제 취소 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 결제 정보",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    public ResponseEntity<ReservationDto> cancelPayment(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.cancelReservation(merchant_uid));
    }

    @GetMapping("client/list")
    @Operation(summary = "예약 리스트 조회", description = "예약 리스트를 조회합니다.", tags = "Client")
    @ApiResponse(
            responseCode = "200",
            description = "예약 리스트 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<ReservationDto>> getReservationList() {
        return ResponseEntity.ok(reservationService.getReservationList());
    }

    @GetMapping("client/{merchant_uid}")
    @Operation(summary = "예약 정보 조회", description = "예약 정보를 조회합니다.", tags = "Client")
    @ApiResponse(
            responseCode = "200",
            description = "예약 정보 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    public ResponseEntity<ReservationDto> getReservation(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.getReservation(merchant_uid));
    }

    @PostMapping("guide/confirm/{merchant_uid}")
    @Operation(summary = "예약 확정", description = "예약을 확정합니다.", tags = "Guide")
    @ApiResponse(
            responseCode = "200",
            description = "예약 확정 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 예약 정보",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.confirmReservation(merchant_uid));
    }

    @PostMapping("guide/cancel/{merchant_uid}")
    @Operation(summary = "예약 취소", description = "예약을 취소합니다.", tags = "Guide")
    @ApiResponse(
            responseCode = "200",
            description = "예약 취소 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 예약 정보",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.cancelReservation(merchant_uid));
    }
}


