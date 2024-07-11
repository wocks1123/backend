package com.swygbro.trip.backend.domain.guideProduct.fixture;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GuideProductRequestFixture {

    public static CreateGuideProductRequest getCreateGuideProductRequest() {
        List<GuideCategoryCode> categories = new ArrayList<>();
        categories.add(GuideCategoryCode.DINING);
        categories.add(GuideCategoryCode.ART_CULTURE);

        return new CreateGuideProductRequest(
                "test title",
                "test description",
                20000L,
                "Seoul",
                50.2,
                100.1,
                LocalDate.of(2024, 4, 28),
                LocalDate.of(2024, 4, 28),
                LocalTime.of(12, 0),
                LocalTime.of(20, 0),
                2,
                categories
        );
    }

    public static ModifyGuideProductRequest getModifyProductRequest() {
        List<GuideCategoryCode> categories = new ArrayList<>();
        categories.add(GuideCategoryCode.DINING);
        categories.add(GuideCategoryCode.ART_CULTURE);

        return new ModifyGuideProductRequest(
                "modify title1",
                "modify description1",
                20000L,
                "Seoul",
                50.2,
                100.1,
                LocalDate.of(2024, 4, 28),
                LocalDate.of(2024, 4, 28),
                LocalTime.of(12, 0),
                LocalTime.of(20, 0),
                3,
                categories,
                null,
                new ArrayList<>()
        );
    }
}
