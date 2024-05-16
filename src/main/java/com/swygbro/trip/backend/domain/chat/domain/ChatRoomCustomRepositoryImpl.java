package com.swygbro.trip.backend.domain.chat.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.swygbro.trip.backend.domain.chat.domain.QChatRoom.chatRoom;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public ChatRoom findChatRoomByClientAndGuide(Long clientId, Long guideId) {
        return queryFactory
                .selectFrom(chatRoom)
                .where(chatRoom.client.id.eq(clientId)
                        .and(chatRoom.guide.id.eq(guideId)))
                .fetchOne();
    }
}
