package com.swygbro.trip.backend.domain.chat.api;

import com.swygbro.trip.backend.domain.chat.application.ChatService;
import com.swygbro.trip.backend.domain.chat.dto.ChatDto;
import com.swygbro.trip.backend.domain.chat.dto.ChatRoomRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/topic/chat/{chatRoomId}")
    public ChatDto message(
            @DestinationVariable Long chatRoomId,
            @Payload ChatDto request) {

        return request;
    }

    @PostMapping("/room")
    // return List<ChatDto>
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    public void joinChatRoom(@CurrentUser User user, ChatRoomRequest chatRoomRequest) {
        chatService.joinChatRoom(user, chatRoomRequest);
    }


}
