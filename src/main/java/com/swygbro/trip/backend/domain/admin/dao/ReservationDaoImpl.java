package com.swygbro.trip.backend.domain.admin.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.admin.dto.ReservationDetailDto;
import com.swygbro.trip.backend.domain.guideProduct.domain.QGuideProduct;
import com.swygbro.trip.backend.domain.reservation.domain.QReservation;
import com.swygbro.trip.backend.domain.user.domain.QUser;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationDaoImpl implements ReservationDao {

    QReservation qReservation = QReservation.reservation;
    QGuideProduct qGuideProduct = QGuideProduct.guideProduct;
    QUser qUser = QUser.user;

    private final JPAQueryFactory queryFactory;

    public Page<ReservationDetailDto> findReservationsByFilter(Pageable pageable,
                                                               Long id,
                                                               String merchantUid,
                                                               Long productId,
                                                               String client,
                                                               String guide,
                                                               PayStatus payStatus,
                                                               ReservationStatus reservationStatus) {

        QUser qUserClient = new QUser("client");
        QUser qUserGuide = new QUser("guide");

        BooleanBuilder builder = createBooleanBuilder(id, merchantUid, productId, client, guide, payStatus, reservationStatus);

        List<ReservationDetailDto> reservations = queryFactory
                .select(Projections.fields(ReservationDetailDto.class,
                        qReservation.id,
                        qReservation.merchantUid,
                        qUserClient.nickname.as("client"),
                        qUserGuide.nickname.as("guide"),
                        qReservation.product.id.as("guideProductId"),
                        qReservation.guideStart,
                        qReservation.guideEnd,
                        qReservation.personnel,
                        qReservation.price,
                        qReservation.paymentStatus,
                        qReservation.reservationStatus,
                        qReservation.createdAt,
                        qReservation.updatedAt))
                .from(qReservation)
                .where(builder)
                .leftJoin(qReservation.client, qUserClient)
                .leftJoin(qReservation.guide, qUserGuide)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(qReservation.count())
                .from(qReservation)
                .where(builder)
                .leftJoin(qReservation.client, qUserClient)
                .leftJoin(qReservation.guide, qUserGuide)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(reservations, pageable, total);
    }


    private BooleanBuilder createBooleanBuilder(Long id,
                                                String merchantUid,
                                                Long productId,
                                                String client,
                                                String guide,
                                                PayStatus payStatus,
                                                ReservationStatus reservationStatus) {


        BooleanBuilder builder = new BooleanBuilder();
        if (id != null) {
            builder.and(qReservation.id.eq(id));
        }
        if (StringUtils.hasText(merchantUid)) {
            builder.and(qReservation.merchantUid.eq(merchantUid));
        }
        if (productId != null) {
            builder.and(qReservation.product.id.eq(productId));
        }
        if (StringUtils.hasText(client)) {
            builder.and(qReservation.client.nickname.eq(client));
        }
        if (StringUtils.hasText(guide)) {
            builder.and(qReservation.guide.nickname.eq(guide));
        }
        if (payStatus != null) {
            builder.and(qReservation.paymentStatus.eq(payStatus));
        }
        if (reservationStatus != null) {
            builder.and(qReservation.reservationStatus.eq(reservationStatus));
        }

        return builder;
    }

}
