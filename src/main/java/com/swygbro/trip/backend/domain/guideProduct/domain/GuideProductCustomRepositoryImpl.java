package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

@Repository
public class GuideProductCustomRepositoryImpl implements GuideProductCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QGuideProduct product = QGuideProduct.guideProduct;

    public GuideProductCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

}
