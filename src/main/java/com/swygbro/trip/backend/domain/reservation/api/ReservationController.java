package com.swygbro.trip.backend.domain.reservation.api;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.swygbro.trip.backend.domain.reservation.aplication.ReservationService;
import com.swygbro.trip.backend.domain.reservation.dto.*;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.exception.ApiErrorResponse;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/client/save")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "예약 정보 저장", description = """
            # 예약 정보 저장
                        
            예약 정보를 저장하고 주문 번호를 반환합니다. 예약 정보 저장 시 가이드ID, 상품ID, 예약 날짜, 인원, 메시지, 가격을 전달합니다.
            각 필드의 제약 조건은 다음과 같습니다.

            | 필드명 | 설명 | 제약조건 | 중복확인 | 예시 |
            |--------|------|----------|----------|------|
            |guideId| 가이드의 ID | 실제 가이드 ID | N | 1 |
            |productId| 상품의 ID | 실제 상품 ID | N | 1 |
            |guideStart| 가이드 시작 날짜 | yyyy-MM-dd HH:mm:ss | N | 2024-05-01 12:00:00 |
            |guideEnd| 가이드 종료 날짜 | yyyy-MM-dd HH:mm:ss | N | 2024-05-01 15:00:00 |
            |personnel| 가이드 신청 인원 | 1보다 큰 정수값 | N | 1 |
            |message| 추가 메세지 | 문자열 | N | 안녕하세요 |
            |price| 가격 | 0보다 큰 정수값 | N | 10000 |
             
            ## 응답
                        
            - 예약 정보 저장 성공 시 `200` 코드와 함께 주문 번호를 문자열로 반환합니다.
            - 가이드 ID 혹은 상품 ID 에 오류가 있을 경우 `409` 에러를 반환합니다.
            - 입력 양식에 오류가 있을 경우 400 에러를 반환합니다.
             
            """, tags = "Reservation-Client")
    @ApiResponse(
            responseCode = "200",
            description = "예약 정보 저장 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @ApiResponse(
            responseCode = "409",
            description = "외래 키 참조 에러",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "존재하지 않는 가이드 ID",
                            value = "{ \"status\" : \"CONFLICT\", \"message\" : \"존재하지 않는 외래키입니다. : 테이블\"}")
            )
    )
    public ResponseEntity<String> saveReservation(@CurrentUser User user,
                                                  @RequestBody @Valid SaveReservationRequest orderDto) {
        log.info("Received orders: {}", orderDto.toString());
        return ResponseEntity.ok(reservationService.saveReservation(user.getId(), orderDto));
    }

    @PostMapping("/client/payment/validation")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "결제 내역 검증", description = """
            # 결제 내역 검증
                        
            - PortOne 서버에 imp_uid 를 사용해 직접 결제 정보를 요청합니다.
             
            ## 응답
                        
            - 결제 내역 조회 시 `200` 코드와 함께 결제 내역을 반환합니다. IamPortResponse<Payment> 형태로 반환됩니다.
            - imp_uid 에 오류가 있을 경우 `404` 에러를 반환합니다.
             
            """, tags = "Reservation-Client")
    @ApiResponse(
            responseCode = "200",
            description = "결제 정보 검증 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = IamportResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 결제 정보입니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "주문 번호가 유효하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"주문번호를 찾을 수 없습니다. : 주문번호\"}")
            ))
    public IamportResponse<Payment> validateIamport(@RequestBody PayValidateRequest payValidateRequest) throws IamportResponseException, IOException {
        return reservationService.validateIamport(payValidateRequest.getImp_uid());
    }


    @PostMapping("/client/payment")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "결제 정보 저장", description = """
            # 결제 정보 저장
                        
            결제 정보를 저장하고 예약 내역을 반환합니다. 주문번호, 상품ID, 결제 고유 번호, 결제 시간, 결제 금액, 결제 수량을 전달합니다.
            각 필드의 제약 조건은 다음과 같습니다.

            | 필드명 | 설명 | 제약조건 | 중복확인 | 예시 |
            |--------|------|----------|----------|------|
            |merchantUid| 주문번호 | - | Y | 20240502-a9s832 |
            |productId| 상품의 ID | 실제 상품 ID | N | 1 |
            |impUid| 결제 고유 번호 | - | Y | imp_76496064148 |
            |paidAt| 결제 시간 | UNIX 타임스탬프 | N | 1648344363 |
            |price| 가격 | 0보다 큰 정수값 | N | 10000 |
            |personnel| 결제 수량 | 1보다 큰 정수값 | N | 1 |
             
            # 참고
                        
            프로세스 간략화를 통해 결제 이후 바로 예약 확정까지 진행합니다.
             
            ## 응답
                        
            - 예약 정보 저장 성공 시 `200` 코드와 함께 주문 번호를 문자열로 반환합니다.
            - 가이드 ID 혹은 상품 ID 에 오류가 있을 경우 `409` 에러를 반환합니다.
            - 입력 양식에 오류가 있을 경우 400 에러를 반환합니다.
             
            """, tags = "Reservation-Client")
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
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "주문 번호가 유효하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"예약 정보를 찾을 수 없습니다. : 주문번호\"}")
            ))
    public ResponseEntity<ReservationDto> processOrder(@RequestBody @Valid SavePaymentRequest orderDto) throws IamportResponseException, IOException {
        log.info("Received orders: {}", orderDto.toString());
        return ResponseEntity.ok(reservationService.savePayment(orderDto));
    }

    @PostMapping("/client/cancel/{merchant_uid}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "예약 취소", description = """
            # 여행객 예약 취소
                        
            - 주문 번호를 입력받아 예약을 취소하고 결제를 취소합니다.
            - 예약의 Status 가 CANCELED 로 변경됩니다.
             
            ## 응답
                        
            - 정상 취소 시 `200` 코드와 함께 예약 내역을 반환합니다.
            - 주문 번호에 오류가 있을 경우 `404` 에러를 반환합니다.
             
            """, tags = "Reservation-Client")
    @ApiResponse(
            responseCode = "200",
            description = "예약 및 결제 취소 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 주문 정보",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "주문 번호가 유효하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"예약 정보를 찾을 수 없습니다. : 주문번호\"}")
            )
    )
    public ResponseEntity<ReservationDto> cancelPayment(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.cancelReservation(merchant_uid));
    }

    @PostMapping("/client/list")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "예약 리스트 조회", description = """
            # 여행객 예약 리스트 조회
                        
            - 여행객의 예약 리스트를 조회합니다.            
                        
            | 필드명 | 설명 | 제약조건  | 예시 |
            |--------|------|----------|----------|
            | isPast | 과거 or 미래 조건 | Boolean  | ture |
            | statusFilter | 예약 Status 조건 | 0(확정 대기), 1(확정 및 정산 완료), 2(취소) | 1 |
            | offset | 조회 offset | -  | 0 |
            | pageSize | 조회 페이지 크기 | -  | 10 |
                        
            ## 응답
                        
            - 정상 조회 시 `200` 코드와 함께 예약 내역 리스트를 반환합니다.
             
            """, tags = "Reservation-Client")
    @ApiResponse(
            responseCode = "200",
            description = "예약 리스트 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<ReservationDto>> getReservationList(@CurrentUser User user, @RequestBody ReservationSearchCriteria criteria) {
        return ResponseEntity.ok(reservationService.getReservationListByClient(user.getId(), criteria));
    }

    @PostMapping("/guide/list")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "예약 리스트 조회", description = """
            # 가이드 예약 조회
                        
            - 가이드가 자신의 예약 리스트를 조회합니다.           
                        
            | 필드명 | 설명 | 제약조건  | 예시 |
            |--------|------|----------|----------|
            | isPast | 과거 or 미래 조건 | Boolean  | ture |
            | statusFilter | 예약 Status 조건 | 0(확정 대기), 1(확정 및 정산 완료), 2(취소) | 1 |
            | offset | 조회 offset | -  | 0 |
            | pageSize | 조회 페이지 크기 | -  | 10 |
                                    
            ## 응답
                        
            - 정상 취소 시 `200` 코드와 함께 예약 내역을 반환합니다.
            """, tags = "Reservation-Guide")
    @ApiResponse(
            responseCode = "200",
            description = "예약 리스트 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<ReservationDto>> getReservationListByGuide(@CurrentUser User user, @RequestBody ReservationSearchCriteria criteria) {
        return ResponseEntity.ok(reservationService.getReservationListByGuide(user.getId(), criteria));
    }

    @PostMapping("/guide/confirm/{merchant_uid}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "예약 확정", description = """
            # 가이드 예약 확정
                        
            - 가이드가 주문 번호를 입력받아 예약을 확정합니다.
            - 예약의 Status 가 CONFIRMED 로 변경됩니다.
             
            ## 응답
                        
            - 예약 확정 시 `200` 코드와 함께 예약 내역을 반환합니다.
            - 주문 번호에 오류가 있을 경우 `404` 에러를 반환합니다.
             
            """, tags = "Reservation-Guide")
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
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "주문 번호가 유효하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"예약 정보를 찾을 수 없습니다. : 주문번호\"}")
            )
    )
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.confirmReservation(merchant_uid));
    }

    @PostMapping("/guide/cancel/{merchant_uid}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "예약 취소", description = """
            # 가이드 예약 취소
                        
            - 주문 번호를 입력받아 예약을 취소하고 결제를 취소합니다.
            - 예약의 Status 가 CANCELED 로 변경됩니다.
             
            ## 응답
                        
            - 정상 취소 시 `200` 코드와 함께 예약 내역을 반환합니다.
            - 주문 번호에 오류가 있을 경우 `404` 에러를 반환합니다.
             
            """, tags = "Reservation-Guide")
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
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "주문 번호가 유효하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"예약 정보를 찾을 수 없습니다. : 주문번호\"}")
            )
    )
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.cancelReservation(merchant_uid));
    }

    @GetMapping("/{merchant_uid}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "예약 정보 조회", description = """
            # 예약 조회
                        
            - 주문 번호를 통해 여행객의 예약을 조회합니다.            
                        
            ## 응답
                        
            - 정상 취소 시 `200` 코드와 함께 예약 내역을 반환합니다.
            - 주문 번호에 오류가 있을 경우 `404` 에러를 반환합니다.
            """, tags = {"Reservation-Client", "Reservation-Guide"})
    @ApiResponse(
            responseCode = "200",
            description = "예약 정보 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReservationDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 주문 정보",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "주문 번호가 유효하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"예약 정보를 찾을 수 없습니다. : 주문번호\"}")
            )
    )
    public ResponseEntity<ReservationDto> getReservation(@PathVariable String merchant_uid) {
        return ResponseEntity.ok(reservationService.getReservation(merchant_uid));
    }

}


