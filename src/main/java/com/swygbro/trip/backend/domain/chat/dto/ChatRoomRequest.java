package com.swygbro.trip.backend.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ChatRoomRequest {
	@Schema(description = "채팅 생성을 위한 가이드 Email", example = "user01@email.com")
	private String email;
}