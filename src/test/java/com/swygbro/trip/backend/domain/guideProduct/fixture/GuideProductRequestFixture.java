package com.swygbro.trip.backend.domain.guideProduct.fixture;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;

import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class GuideProductRequestFixture {

    public static CreateGuideProductRequest getCreateGuideProductRequest() {
        List<GuideCategoryCode> categories = new ArrayList<>();
        categories.add(GuideCategoryCode.DINING);
        categories.add(GuideCategoryCode.ART_CULTURE);

        return new CreateGuideProductRequest(
                "test@gmail.com",
                "test title",
                "test description",
                20000L,
                50.2,
                100.1,
                Year.of(2024).atMonth(4).atDay(28).atTime(23, 0).atZone(ZoneId.of("Asia/Seoul")),
                Year.of(2024).atMonth(4).atDay(28).atTime(23, 0).atZone(ZoneId.of("Asia/Seoul")),
                categories
        );
    }

    public static ModifyGuideProductRequest getModifyProductRequest() {
        List<GuideCategoryCode> categories = new ArrayList<>();
        categories.add(GuideCategoryCode.DINING);
        categories.add(GuideCategoryCode.ART_CULTURE);

        return new ModifyGuideProductRequest(
                "test@gmail.com",
                "modify title1",
                "modify description1",
                20000L,
                50.2,
                100.1,
                Year.of(2024).atMonth(4).atDay(28).atTime(23, 0).atZone(ZoneId.of("Asia/Seoul")),
                Year.of(2024).atMonth(4).atDay(28).atTime(23, 0).atZone(ZoneId.of("Asia/Seoul")),
                categories,
                null,
                new ArrayList<>()
        );
    }
}
