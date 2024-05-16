package com.swygbro.trip.backend.domain.chat.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatHistoryRepository extends MongoRepository<ChatHistory, Long> {
	List<ChatHistory> findByChatRoomId(Long chatRoomId);
}