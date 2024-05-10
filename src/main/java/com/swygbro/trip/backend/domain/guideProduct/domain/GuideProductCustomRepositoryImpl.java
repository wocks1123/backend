package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.domain.QUser;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class GuideProductCustomRepositoryImpl implements GuideProductCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QGuideProduct product = QGuideProduct.guideProduct;
    private final QGuideCategory category = QGuideCategory.guideCategory;
    private final QUser user = QUser.user;

    public GuideProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<GuideProduct> findDetailById(Long productId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(product)
                .join(product.categories, category).fetchJoin()
                .where(product.id.eq(productId))
                .fetchOne());
    }

    @Override
    public List<GuideProduct> findAllByLocation(Point point, int radius) {
        return jpaQueryFactory.selectFrom(product)
                .join(product.categories, category).fetchJoin()
                .where(nearGuideProduct(point, radius))
                .fetch();
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
                                           Nationality nationality) {
        return jpaQueryFactory.selectFrom(product)
                .join(product.categories, category).fetchJoin()
                .where(regionEq(region),
                        categoryIn(categories),
                        product.guideStart.between(start, end),
                        product.price.between(minPrice, maxPrice),
                        createTimeDiffCondition(minDuration, maxDuration),
                        hourEq(dayTime),
                        nationalityEq(nationality))
                .fetch();
    }

    private BooleanExpression nearGuideProduct(Point center, int radius) {
        return Expressions.booleanTemplate("ST_CONTAINS(ST_BUFFER({0}, {1}), {2})",
                center, radius, product.location);
    }

    private BooleanExpression createTimeDiffCondition(int minDuration, int maxDuration) {
        return Expressions.booleanTemplate("TIMESTAMPDIFF(HOUR, {0}, {1}) between {2} and {3}",
                product.guideStart, product.guideEnd, minDuration, maxDuration);
    }

    private BooleanExpression hourEq(DayTime dayTime) {
        return Expressions.booleanTemplate("DATE_FORMAT(convert_tz({0}, '+00:00', '+09:00'), '%H:%i:%s') between {1} and {2}",
                product.guideStart, dayTime.getStart(), dayTime.getEnd());
    }

    private BooleanExpression regionEq(MultiPolygon region) {
        return Expressions.booleanTemplate("ST_CONTAINS({0}, {1})",
                region, product.location);
    }

    private BooleanExpression categoryIn(List<GuideCategoryCode> categories) {
        if (categories != null) return category.categoryCode.in(categories);
        return null;
    }

    private BooleanExpression nationalityEq(Nationality nationality) {
        if (nationality != null) return product.user.nationality.eq(nationality);
        return null;
    }
}
