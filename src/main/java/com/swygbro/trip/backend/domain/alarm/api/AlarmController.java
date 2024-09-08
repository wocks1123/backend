package com.swygbro.trip.backend.domain.alarm.api;

import com.swygbro.trip.backend.domain.alarm.application.AlarmService;
import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/alarm/subscribe")
    @PreAuthorize("isAuthenticated() and hasRole('USER') and #user.id == principal.id")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "알람 SSE 연결", description = """
            # 알람 SSE 연결
                        
            알람 서비스를 이용하기 위해선 SSE 연결 api입니다.
            SSE는 연결 후 1시간 이후 연결이 종료 됩니다.
                        
            SSE 연결 시 Event name은 'open' 입니다.
            SSE 연결 후 SSE 미연결 시 수신받은 알람이 존재할 경우 Event name은 'alarm' 입니다.
                        
            ## 응답
                        
            - 연결 성공 시 'connect completed' 를 반환합니다.
            - SSE 미연결 시 수신받은 알람이 존재할 경우 연결 성공 시 수신받은 알람을 전송합니다.
            - 연결 실패 시 '500' 에러를 반환합니다.
            """, tags = "Alarm")
    @ApiResponse(
            responseCode = "200",
            description = "알람 연결 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SseEmitter.class),
                    examples = {@ExampleObject(name = "알람 연결 성공", value = "{ \"connect completed\"}"),
                            @ExampleObject(name = "수신 받지 못한 알람 전송",
                                    value = "{\"AlarmId\" : \"알람 ID\", \"FromUserId\" : \"알람을 보낸 유저 ID\", \"TargetId\" : \"타깃 ID\", \"AlarmType\" : \"리뷰, 예약등 알람 타입\", \"isRead\" : \"알람 읽음 여부\" }"
                            )}
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "알람 연결 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiResponse.class),
                    examples = @ExampleObject(
                            name = "알람 연결 실패",
                            value = "{ \"status\" : \"INTERNAL_SERVER_ERROR\", \"message\" : \"해당 유저와 연결에 실패했습니다\"}"
                    )
            )
    )
    public SseEmitter subscribe(@CurrentUser User user) {
        return alarmService.connectAlarm(user.getId());
    }

    @GetMapping("/alarm")
    @PreAuthorize("isAuthenticated() and hasRole('USER') and #user.id == principal.id")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "알람 조회", description = """
            # 알람 조회
                        
            수신 받은 알람 리스트를 조회합니다.
                        
            size = 5, sort = createdAt, 내림차순 방식으로 페이징 합니다.
                        
            ## 각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | 예시 |
            |--------|------|----------|----------|
            |isRead| 알람 확인 유무 | Integer(required = false) | 1 |
                        
            알람 확인 유무 조건 없이 모든 리스트를 불러오고 싶을땐 isRead 입력X
            isRead가 0일 경우 알람 확인을 안한 알람 리스트 조회
            isRead가 1일 경우 알람 확인을 한 알람 리스트 조회
                        
            ## 응답
                        
            - 조회 성공 시 `200` 코드와 함께 알람 리스트 정보를 json 형태로 반환합니다.
            """, tags = "Alarm")
    @ApiResponse(
            responseCode = "200",
            description = "알람 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AlarmDto.class)
            )
    )
    public Page<AlarmDto> getAlarmList(@CurrentUser User user,
                                       @RequestParam(required = false) Integer isRead,
                                       @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return alarmService.getAlarmList(user.getId(), isRead, pageable);
    }
}
