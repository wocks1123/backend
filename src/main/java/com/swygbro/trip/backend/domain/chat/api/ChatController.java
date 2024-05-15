package com.swygbro.trip.backend.domain.chat.api;

import com.swygbro.trip.backend.domain.chat.application.ChatHistoryService;
import com.swygbro.trip.backend.domain.chat.application.ChatService;
import com.swygbro.trip.backend.domain.chat.dto.ChatDto;
import com.swygbro.trip.backend.domain.chat.dto.ChatRequest;
import com.swygbro.trip.backend.domain.chat.dto.ChatRoomRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat")
public class ChatController {
	private final ChatService chatService;
	private final ChatHistoryService chatHistoryService;

	@MessageMapping("/chat/{chatRoomId}")
	@SendTo("/topic/chat/{chatRoomId}")
	public ChatRequest message(
			@DestinationVariable Long chatRoomId,
			@Payload ChatRequest request) {
		chatHistoryService.saveChat(request);

		return request;
	}

	@PostMapping("/room")
	@PreAuthorize("isAuthenticated() and hasRole('USER')")
	@SecurityRequirement(name = "access-token")
	@Operation(summary = "채팅방 생성", description = """
					# 채팅방 생성 및 히스토리 조회
			                   
			       - 가이드와의 채팅방 생성 혹은 기존 채팅방 조회를 위한 API 입니다.
			       - 여행객만 가이드와의 채팅방을 생성할 수 있습니다.
			       - 가이드의 이메일 정보를 통해 채팅방을 생성하거나 조회합니다.
			                    
			       ## 응답
			                   
			       - 정상 조회 시 `200` 코드와 함께 채팅 내역을 반환합니다.
			           - 새로운 채팅방일 경우 빈 리스트를 반환합니다. 
						
			""")
	public List<ChatDto> joinChatRoom(@CurrentUser User user, ChatRoomRequest chatRoomRequest) {
		return chatService.joinChatRoom(user, chatRoomRequest);
	}
}