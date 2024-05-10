package com.swygbro.trip.backend.domain.reservation.domain;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

import static com.swygbro.trip.backend.domain.reservation.domain.QReservation.reservation;

@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Reservation> findPastReservationsByClientId(Long clientId) {
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.client.id.eq(clientId)
                        .and(reservation.reservatedAt.before(
                                Expressions.dateTimeOperation(
                                        ZonedDateTime.class,
                                        Ops.DateTimeOps.CURRENT_TIMESTAMP
                                )
                        )))
                .fetch();
    }

    @Override
    public List<Reservation> findFutureReservationsByClientId(Long clientId) {
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.client.id.eq(clientId)
                        .and(reservation.reservatedAt.after(
                                Expressions.dateTimeOperation(
                                        ZonedDateTime.class,
                                        Ops.DateTimeOps.CURRENT_TIMESTAMP
                                )
                        )))
                .fetch();
    }

    @Override
    public List<Reservation> findPastReservationsByGuideId(Long guideId) {
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.guide.id.eq(guideId)
                        .and(reservation.reservatedAt.before(
                                Expressions.dateTimeOperation(
                                        ZonedDateTime.class,
                                        Ops.DateTimeOps.CURRENT_TIMESTAMP
                                )
                        )))
                .fetch();
    }

    @Override
    public List<Reservation> findFutureReservationsByGuideId(Long guideId) {
        return queryFactory
                .selectFrom(reservation)
                .where(reservation.guide.id.eq(guideId)
                        .and(reservation.reservatedAt.after(
                                Expressions.dateTimeOperation(
                                        ZonedDateTime.class,
                                        Ops.DateTimeOps.CURRENT_TIMESTAMP
                                )
                        )))
                .fetch();
    }
}
