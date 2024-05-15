package com.swygbro.trip.backend.domain.chat.application;

import com.swygbro.trip.backend.domain.chat.domain.ChatRoom;
import com.swygbro.trip.backend.domain.chat.domain.ChatRoomRepository;
import com.swygbro.trip.backend.domain.chat.dto.ChatDto;
import com.swygbro.trip.backend.domain.chat.dto.ChatRoomRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatRoomRepository chatRoomRepository;
	private final UserRepository userRepository;
	private final ChatHistoryService chatHistoryService;

	public List<ChatDto> joinChatRoom(User user, ChatRoomRequest chatRoomRequest) {
		User guide = userRepository.findByEmail(chatRoomRequest.getEmail()).orElseThrow(
				() -> new UserNotFoundException(chatRoomRequest.getEmail())
		);

		ChatRoom chatRoom = chatRoomRepository.findChatRoomByClientAndGuide(user.getId(), guide.getId());
		if (chatRoom == null) {
			chatRoom = ChatRoom.builder()
					.client(user)
					.guide(guide)
					.build();
			chatRoom = chatRoomRepository.save(chatRoom);
			return List.of();
		}

		return chatHistoryService.getChatHistory(chatRoom.getId());
	}
}