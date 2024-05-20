package com.swygbro.trip.backend.domain.admin.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.admin.dto.ReviewDetailDto;
import com.swygbro.trip.backend.domain.review.domain.QReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {

    private final JPAQueryFactory queryFactory;

    QReview qReview = QReview.review;

    public Page<ReviewDetailDto> findReviewsByFilter(Pageable pageable,
                                                     Long id,
                                                     String reviewer,
                                                     Long guideProductId) {

        BooleanBuilder builder = new BooleanBuilder();
        if (id != null) {
            builder.and(qReview.id.eq(id));
        }
        if (StringUtils.hasText(reviewer)) {
            builder.and(qReview.reviewer.nickname.contains(reviewer));
        }
        if (guideProductId != null) {
            builder.and(qReview.guideProduct.id.eq(guideProductId));
        }

        var reviews = queryFactory
                .select(Projections.fields(ReviewDetailDto.class,
                        qReview.id,
                        qReview.reviewer.nickname.as("reviewer"),
                        qReview.guideProduct.id.as("guideProductId"),
                        qReview.rating,
                        qReview.content,
                        qReview.createdAt,
                        qReview.updatedAt))
                .from(qReview)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(qReview.count())
                .from(qReview)
                .where(builder)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(reviews, pageable, total);
    }
}
