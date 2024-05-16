package com.swygbro.trip.backend.domain.chat.domain;

public interface ChatRoomCustomRepository {
    ChatRoom findChatRoomByClientAndGuide(Long clientId, Long guideId);
}
