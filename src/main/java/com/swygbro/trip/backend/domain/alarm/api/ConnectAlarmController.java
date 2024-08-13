package com.swygbro.trip.backend.domain.alarm.api;

import com.swygbro.trip.backend.domain.alarm.application.AlarmService;
import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import com.swygbro.trip.backend.domain.reservation.domain.Reservation;
import com.swygbro.trip.backend.domain.review.domain.Review;
import com.swygbro.trip.backend.global.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConnectAlarmController {

    private final AlarmService alarmService;

    @PostMapping("/alarm/{alarmId}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "알람 클릭 시 타깃으로 이동", description = """
            # 알람 클릭 시 타깃으로 이동
                        
            알람 클릭 시 해당 알람 타깃으로 이동합니다.
            리뷰 알람일 경우 클릭 시 해당 리뷰로 이동.
            예약 알람일 경우 클릭 시 해당 알람으로 이동.
                        
            ## 각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | 예시 |
            |--------|------|----------|----------|
            |alarmId| 알람 id | 숫자 | 1 |
                                               
            ## 응답
                        
            - 해당 타깃 정보를 조회해 줍니다.
            - 잘못된 alarmType 입력 시 '404' 에러를 반환합니다.
            - 리뷰, 예약 조회 시 발생하는 에러는 해당 api를 확인 해주세요.
            """, tags = "Alarm")
    @ApiResponse(
            responseCode = "200",
            description = "타깃 리뷰 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Review.class)
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "타깃 예약 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Reservation.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "조회 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BaseException.class),
                    examples = @ExampleObject(
                            name = "가이드 상품 조회 실패",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"해당 알람을 찾을 수 없습니다.\"}")
            )
    )
    public String getAlarm(@PathVariable Long alarmId) {

        AlarmDto alarmDto = alarmService.getAlarm(alarmId);
        String path = null;
        switch (alarmDto.getAlarmType().getType()) {
            case "reservation" -> {
                path = "redirect:/api/v1/reservation/" + alarmDto.getArgs().getTargetId();
            }
            case "review" -> {
                path = "redirect:/api/v1/reviews/" + alarmDto.getArgs().getTargetId();
            }
        }

        return path;
    }
}
