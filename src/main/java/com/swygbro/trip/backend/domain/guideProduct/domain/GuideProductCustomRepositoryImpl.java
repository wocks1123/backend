package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.guideProduct.dto.SearchCategoriesRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.SearchGuideProductResponse;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import org.locationtech.jts.geom.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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
    public List<SearchGuideProductResponse> findByLocation(Geometry geometry, int radius) {
        return jpaQueryFactory
                .select(Projections.fields(SearchGuideProductResponse.class,
                        qProduct.id,
                        qProduct.title,
                        qProduct.thumb,
                        qProduct.guideStart,
                        qProduct.guideEnd))
                .from(qProduct)
                .where(nearGuideProduct(geometry, radius))
                .limit(4)
                .distinct().fetch();
    }

    @Override
    public List<SearchGuideProductResponse> findByBest(MultiPolygon polygon) {
        return jpaQueryFactory
                .select(Projections.fields(SearchGuideProductResponse.class,
                        qProduct.id,
                        qProduct.title,
                        qProduct.thumb,
                        qProduct.guideStart,
                        qProduct.guideEnd))
                .from(qProduct)
                .where(regionEq(polygon))
                .limit(4)
                .distinct().fetch();
    }

    @Override
    public Page<SearchGuideProductResponse> findAllWithMain(Pageable pageable) {
        List<SearchGuideProductResponse> fetch = jpaQueryFactory
                .select(Projections.fields(SearchGuideProductResponse.class,
                        qProduct.id,
                        qProduct.title,
                        qProduct.thumb,
                        qProduct.guideStart,
                        qProduct.guideEnd))
                .from(qProduct)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct().fetch();

        JPQLQuery<Long> count = jpaQueryFactory.select(qProduct.count())
                .from(qProduct);

        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    @Override
    public Page<SearchGuideProductResponse> findByFilter(MultiPolygon region,
                                                         ZonedDateTime start,
                                                         ZonedDateTime end,
                                                         SearchCategoriesRequest category,
                                                         Long minPrice,
                                                         Long maxPrice,
                                                         int minDuration,
                                                         int maxDuration,
                                                         DayTime dayTime,
                                                         Nationality nationality,
                                                         Pageable pageable) {
        List<SearchGuideProductResponse> fetch = jpaQueryFactory
                .select(Projections.fields(SearchGuideProductResponse.class,
                        qProduct.id,
                        qProduct.title,
                        qProduct.thumb,
                        qProduct.guideStart,
                        qProduct.guideEnd))
                .from(qProduct)
                .where(regionEq(region),
                        startDateBetween(start, end),
                        categoryIn(region, category),
                        qProduct.price.between(minPrice, maxPrice),
                        qProduct.guideTime.between(minDuration, maxDuration),
                        hourEq(dayTime),
                        nationalityEq(nationality))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct().fetch();

        JPQLQuery<Long> count = jpaQueryFactory.select(qProduct.count())
                .from(qProduct)
                .where(regionEq(region),
                        startDateBetween(start, end),
                        categoryIn(region, category),
                        qProduct.price.between(minPrice, maxPrice),
                        qProduct.guideTime.between(minDuration, maxDuration),
                        hourEq(dayTime),
                        nationalityEq(nationality));

        return PageableExecutionUtils.getPage(fetch, pageable, count::fetchOne);
    }

    private BooleanExpression nearGuideProduct(Geometry geometry, int radius) {
        if (geometry.getGeometryType().equals("Point")) {
            return Expressions.booleanTemplate("ST_CONTAINS(ST_BUFFER({0}, {1}), {2})",
                    geometry, radius, qProduct.location);
        } else return Expressions.booleanTemplate("ST_CONTAINS({0}, {1})",
                geometry, qProduct.location);
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
            else return qProduct.id.in(jpaQueryFactory.select(qCategory.product.id)
                        .from(qCategory)
                        .where(qCategory.categoryCode.eq(request.getCategory())));
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
