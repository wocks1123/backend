package com.swygbro.trip.backend.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


@Getter
public class ChatRequest {

    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @Schema(description = "메시지를 보낸 사용자 Email", example = "user01@email.com")
    private Long senderEmail;

    @Schema(description = "메시지", example = "Hello, World!")
    private String message;

    @Schema(description = "메시지 타입", example = "MESSAGE")
    private MessageType type;
}
