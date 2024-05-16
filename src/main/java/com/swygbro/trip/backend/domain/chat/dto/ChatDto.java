package com.swygbro.trip.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private String senderEmail;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private String payload;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private MessageType type;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;
}