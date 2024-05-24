package com.swygbro.trip.backend.domain.reservation.domain;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.reservation.dto.ReservationSearchCriteria;
import com.swygbro.trip.backend.global.status.ReservationStatus;
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
    public List<Reservation> findReservationsByClientId(Long clientId, ReservationSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        timeFilter(criteria.getTimeFilter(), builder);
        statusFilter(criteria.getStatusFilter(), builder);

        return queryFactory
                .selectFrom(reservation)
                .where(reservation.client.id.eq(clientId)
                        .and(builder))
                .offset(criteria.getOffset())
                .limit(criteria.getPageSize())
                .fetch();
    }


    @Override
    public List<Reservation> findReservationsByGuideId(Long guideId, ReservationSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        timeFilter(criteria.getTimeFilter(), builder);
        statusFilter(criteria.getStatusFilter(), builder);

        return queryFactory
                .selectFrom(reservation)
                .where(reservation.guide.id.eq(guideId)
                        .and(builder))
                .offset(criteria.getOffset())
                .limit(criteria.getPageSize())
                .fetch();
    }

    private static void statusFilter(int statusFilter, BooleanBuilder builder) {
        switch (statusFilter) {
            case 1: // 확정 대기 중인 예약
                builder.and(reservation.reservationStatus.eq(ReservationStatus.PENDING_CONFIRMATION));
                break;
            case 2: // 확정된 예약
                builder.and(reservation.reservationStatus.eq(ReservationStatus.RESERVED)
                        .or(reservation.reservationStatus.eq(ReservationStatus.SETTLED)));
                break;
            case 3: // 취소된 예약
                builder.and(reservation.reservationStatus.eq(ReservationStatus.CANCELLED));
                break;
            default: //모든 예약
                break;
        }
    }

    private static void timeFilter(int timeFilter, BooleanBuilder builder) {
        if (timeFilter == 0) {
            builder.and(isBeforeFromNow());
        } else if (timeFilter == 1) {
            builder.and(isAfterFromNow());
        }
    }

    private static BooleanExpression isBeforeFromNow() {
        return reservation.guideStart.before(
                Expressions.dateTimeOperation(
                        ZonedDateTime.class,
                        Ops.DateTimeOps.CURRENT_TIMESTAMP
                )
        );
    }

    private static BooleanExpression isAfterFromNow() {
        return reservation.guideEnd.after(
                Expressions.dateTimeOperation(
                        ZonedDateTime.class,
                        Ops.DateTimeOps.CURRENT_TIMESTAMP
                )
        );
    }

}
