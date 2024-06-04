package com.swygbro.trip.backend.domain.chat.domain;

import com.swygbro.trip.backend.domain.chat.dto.MessageType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chatHistory")
@Builder
@Getter
public class ChatHistory {
	@Id
	private String id;
	private Long chatRoomId;
	private Long senderId;
	private MessageType messageType;
	private String payload;

	@CreatedDate
	private LocalDateTime createdAt;
}