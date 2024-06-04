package com.swygbro.trip.backend.domain.chat.application;

import com.swygbro.trip.backend.domain.chat.domain.ChatHistory;
import com.swygbro.trip.backend.domain.chat.domain.ChatHistoryRepository;
import com.swygbro.trip.backend.domain.chat.dto.ChatDto;
import com.swygbro.trip.backend.domain.chat.dto.ChatRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {
	private final ChatHistoryRepository chatHistoryRepository;
	private final UserRepository userRepository;

	public void saveChat(ChatRequest chatRequest) {
		ChatHistory chatHistory = ChatHistory.builder()
				.chatRoomId(chatRequest.getChatRoomId())
				.senderId(userRepository.findByEmail(chatRequest.getSenderEmail()).get().getId())
				.payload(chatRequest.getPayload())
				.messageType(chatRequest.getType())
				.build();

		chatHistoryRepository.save(chatHistory);
	}

	public List<ChatDto> getChatHistory(Long chatRoomId) {
		List<com.swygbro.trip.backend.domain.chat.domain.ChatHistory> chatHistory = chatHistoryRepository.findByChatRoomId(chatRoomId);
		return chatHistory.stream().map(
				chat -> {
					User chatUser = userRepository.findById(chat.getSenderId()).orElseThrow(
							() -> new UserNotFoundException(chat.getSenderId())
					);

					return ChatDto.builder()
							.senderEmail(chatUser.getEmail())
							.payload(chat.getPayload())
							.type(chat.getMessageType())
							.createdAt(chat.getCreatedAt())
							.build();
				}
		).collect(Collectors.toList());
	}
}