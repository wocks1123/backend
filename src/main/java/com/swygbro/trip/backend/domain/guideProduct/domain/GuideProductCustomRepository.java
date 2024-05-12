package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.swygbro.trip.backend.domain.guideProduct.dto.SearchCategoriesRequest;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface GuideProductCustomRepository {

    Optional<GuideProduct> findDetailById(Long productId);

    List<GuideProduct> findByLocation(Geometry geometry, int radius);

    Page<GuideProduct> findByFilter(MultiPolygon region, ZonedDateTime start, ZonedDateTime end,
                                    SearchCategoriesRequest categories, Long minPrice, Long maxPrice,
                                    int minDuration, int maxDuration, DayTime dayTime,
                                    Nationality nationality, Pageable pageable);

    List<GuideProduct> findByBest(MultiPolygon polygon);

    Page<GuideProduct> findAllWithMain(Pageable pageable);
}
