package com.swygbro.trip.backend.domain.guideProduct.fixture;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideImage;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.user.domain.User;

import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class GuideProductFixture {

    public static GuideProduct getGuideProduct() {
        List<GuideCategory> categories = new ArrayList<>();
        categories.add(new GuideCategory(GuideCategoryCode.C1));
        categories.add(new GuideCategory(GuideCategoryCode.C2));

        List<GuideImage> images = new ArrayList<>();
        images.add(new GuideImage("test url1"));
        images.add(new GuideImage("test url2"));

        GuideProduct product = new GuideProduct(
                new User("account1", "testemail", "testpassword"),
                "test title",
                "test description",
                20000L,
                50.2,
                100.1,
                Year.of(2024).atMonth(4).atDay(28).atTime(20, 0, 0).atZone(ZoneId.of("Asia/Seoul")),
                Year.of(2024).atMonth(4).atDay(28).atTime(23, 0, 0).atZone(ZoneId.of("Asia/Seoul")));

        categories.forEach(product::addGuideCategory);
        images.forEach(product::addGuideImage);

        return product;
    }
}
