package com.swygbro.trip.backend.domain.admin.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.admin.dto.GuideProductDetailDto;
import com.swygbro.trip.backend.domain.guideProduct.domain.QGuideProduct;
import com.swygbro.trip.backend.domain.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GuideProductImpl implements GuideProductDao {
    private final JPAQueryFactory queryFactory;

    public Page<GuideProductDetailDto> findGuideProductsByFilter(Pageable pageable,
                                                                 Long id,
                                                                 String title,
                                                                 String nickname,
                                                                 String locationName) {
        QUser qUser = QUser.user;
        QGuideProduct qGuideProduct = QGuideProduct.guideProduct;

        BooleanBuilder builder = createBooleanBuilder(id, title, nickname, locationName);


        var guideProducts = queryFactory
                .select(Projections.fields(GuideProductDetailDto.class,
                        qGuideProduct.id,
                        qGuideProduct.title,
                        qUser.nickname.as("nickname"),
                        qGuideProduct.description,
                        qGuideProduct.locationName,
                        qGuideProduct.price,
                        qGuideProduct.guideStart,
                        qGuideProduct.guideEnd,
                        qGuideProduct.guideTime,
                        qGuideProduct.createdAt,
                        qGuideProduct.updatedAt))
                .from(qGuideProduct)
                .where(builder)
                .leftJoin(qGuideProduct.user, qUser)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(qGuideProduct.count())
                .from(qGuideProduct)
                .where(builder)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(guideProducts, pageable, total);
    }

    private BooleanBuilder createBooleanBuilder(Long id,
                                                String title,
                                                String nickname,
                                                String locationName) {
        BooleanBuilder builder = new BooleanBuilder();
        if (id != null) {
            builder.and(QGuideProduct.guideProduct.id.eq(id));
        }

        if (StringUtils.hasText(title)) {
            builder.and(QGuideProduct.guideProduct.title.contains(title));
        }

        if (StringUtils.hasText(nickname)) {
            builder.and(QUser.user.nickname.contains(nickname));
        }

        if (StringUtils.hasText(locationName)) {
            builder.and(QGuideProduct.guideProduct.locationName.contains(locationName));
        }

        return builder;
    }

}
