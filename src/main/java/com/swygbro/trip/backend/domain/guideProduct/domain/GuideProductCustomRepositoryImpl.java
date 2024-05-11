package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.guideProduct.dto.SearchCategoriesRequest;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class GuideProductCustomRepositoryImpl implements GuideProductCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QGuideProduct qProduct = QGuideProduct.guideProduct;
    private final QGuideCategory qCategory = QGuideCategory.guideCategory;

    public GuideProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<GuideProduct> findDetailById(Long productId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(qProduct)
                .join(qProduct.categories, qCategory).fetchJoin()
                .where(qProduct.id.eq(productId))
                .fetchOne());
    }

    @Override
    public List<GuideProduct> findAllByLocation(Point point, int radius) {
        return jpaQueryFactory.selectFrom(qProduct)
                .join(qProduct.categories, qCategory).fetchJoin()
                .where(nearGuideProduct(point, radius))
                .fetch();
    }

    @Override
    public List<GuideProduct> findByFilter(MultiPolygon region,
                                           ZonedDateTime start,
                                           ZonedDateTime end,
                                           SearchCategoriesRequest category,
                                           Long minPrice,
                                           Long maxPrice,
                                           int minDuration,
                                           int maxDuration,
                                           DayTime dayTime,
                                           Nationality nationality) {
        return jpaQueryFactory.selectFrom(qProduct)
                .join(qProduct.categories, qCategory).fetchJoin()
                .where(regionEq(region),
                        startDateBetween(start, end),
                        categoryIn(region, category),
                        qProduct.price.between(minPrice, maxPrice),
                        createTimeDiffCondition(minDuration, maxDuration),
                        hourEq(dayTime),
                        nationalityEq(nationality))
                .fetch();
    }

    private BooleanExpression nearGuideProduct(Point center, int radius) {
        return Expressions.booleanTemplate("ST_CONTAINS(ST_BUFFER({0}, {1}), {2})",
                center, radius, qProduct.location);
    }

    private BooleanExpression createTimeDiffCondition(int minDuration, int maxDuration) {
        return Expressions.booleanTemplate("TIMESTAMPDIFF(HOUR, {0}, {1}) between {2} and {3}",
                qProduct.guideStart, qProduct.guideEnd, minDuration, maxDuration);
    }

    private BooleanExpression hourEq(DayTime dayTime) {
        return Expressions.booleanTemplate("DATE_FORMAT(convert_tz({0}, '+00:00', '+09:00'), '%H:%i:%s') between {1} and {2}",
                qProduct.guideStart, dayTime.getStart(), dayTime.getEnd());
    }

    private BooleanExpression regionEq(MultiPolygon region) {
        if (region != null) return Expressions.booleanTemplate("ST_CONTAINS({0}, {1})",
                region, qProduct.location);
        return null;
    }

    private BooleanExpression categoryIn(MultiPolygon region, SearchCategoriesRequest request) {
        if (request.getCategory() != null) {
            if (request.getCategory() == GuideCategoryCode.NEAR) {
                if (request.getLatitude() != null && request.getLongitude() != null) {
                    GeometryFactory geometryFactory = new GeometryFactory();
                    Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
                    point.setSRID(4326);

                    return Expressions.booleanTemplate("ST_CONTAINS(ST_BUFFER({0}, {1}), {2})",
                            point, 30000, qProduct.location);
                } else return Expressions.booleanTemplate("ST_CONTAINS({0}, {1})",
                        region, qProduct.location);
            } else if (request.getCategory() == GuideCategoryCode.BEST)
                return Expressions.booleanTemplate("ST_CONTAINS({0}, {1})",
                        region, qProduct.location);
            else return qCategory.categoryCode.eq(request.getCategory());
        }
        return null;
    }

    private BooleanExpression startDateBetween(ZonedDateTime start, ZonedDateTime end) {
        if (start != null && end != null) return qProduct.guideStart.between(start, end);
        return null;
    }

    private BooleanExpression nationalityEq(Nationality nationality) {
        if (nationality != null) return qProduct.user.nationality.eq(nationality);
        return null;
    }
}
