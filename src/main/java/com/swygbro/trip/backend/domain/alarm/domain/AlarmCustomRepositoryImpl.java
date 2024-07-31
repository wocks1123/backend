package com.swygbro.trip.backend.domain.alarm.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.alarm.dto.AlarmDto;
import com.swygbro.trip.backend.domain.user.domain.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlarmCustomRepositoryImpl implements AlarmCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QAlarm qAlarm = QAlarm.alarm;
    private final QUser qUser = QUser.user;

    public AlarmCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<AlarmDto> findAllByUserId(Long userId, Pageable pageable) {
        List<AlarmDto> fetch = jpaQueryFactory
                .select(Projections.fields(AlarmDto.class,
                        qAlarm.id,
                        qAlarm.alarmType,
                        qAlarm.args))
                .from(qAlarm)
                .where(qAlarm.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct().fetch();

        JPQLQuery<Long> count = jpaQueryFactory.select(qAlarm.count()).from(qAlarm);

        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }
}
