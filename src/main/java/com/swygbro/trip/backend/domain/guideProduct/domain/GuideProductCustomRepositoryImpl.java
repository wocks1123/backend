package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class GuideProductCustomRepositoryImpl implements GuideProductCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QGuideProduct product = QGuideProduct.guideProduct;
    private final QGuideCategory category = QGuideCategory.guideCategory;

    public GuideProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<GuideProduct> findByFilter(MultiPolygon region,
                                           ZonedDateTime start,
                                           ZonedDateTime end,
                                           List<GuideCategoryCode> categories,
                                           Long minPrice,
                                           Long maxPrice,
                                           int minDuration,
                                           int maxDuration,
                                           DayTime dayTime,
                                           boolean same) {
        return jpaQueryFactory.selectFrom(product)
                .join(product.categories, category).fetchJoin()
                .where(regionEq(region),
                        categoryIn(categories),
                        hourEq(start, end),
                        product.price.between(minPrice, maxPrice),
                        createTimeDiffCondition(minDuration, maxDuration),
                        hourEq(dayTime.getStart(), dayTime.getEnd()))
                .fetch();
    }

    private BooleanExpression createTimeDiffCondition(int minDuration, int maxDuration) {
        return Expressions.booleanTemplate("TIMESTAMPDIFF(HOUR, {0}, {1}) between {2} and {3}",
                product.guideStart, product.guideEnd, minDuration, maxDuration);
    }

    private BooleanExpression hourEq(int start, int end) {
        return Expressions.booleanTemplate("{0} between {1} and {2}",
                product.guideStart.hour().add(9), start, end);
    }

    private BooleanExpression hourEq(ZonedDateTime start, ZonedDateTime end) {
        return Expressions.booleanTemplate("convert_tz({0}, '+00:00', '+09:00') between {1} and {2}",
                product.guideStart, start, end);
    }

    private BooleanExpression regionEq(MultiPolygon region) {
        return Expressions.booleanTemplate("ST_CONTAINS({0}, {1})",
                region, product.location);
    }

    private BooleanExpression categoryIn(List<GuideCategoryCode> categories) {
        if (categories != null) return category.categoryCode.in(categories);
        return null;
    }
}
