package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.swygbro.trip.backend.domain.user.domain.Nationality;
import org.locationtech.jts.geom.MultiPolygon;

import java.time.ZonedDateTime;
import java.util.List;

public interface GuideProductCustomRepository {

    List<GuideProduct> findByFilter(MultiPolygon region, ZonedDateTime start, ZonedDateTime end, List<GuideCategoryCode> categories, Long minPrice, Long maxPrice, int minDuration, int maxDuration, DayTime dayTime, Nationality nationality);
}
