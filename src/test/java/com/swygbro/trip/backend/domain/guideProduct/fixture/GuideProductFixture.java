package com.swygbro.trip.backend.domain.guideProduct.fixture;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.user.domain.User;

import java.util.ArrayList;
import java.util.List;

public class GuideProductFixture {

    public static GuideProduct getGuideProduct(CreateGuideProductRequest request) {
        User user = User.builder().email(request.getEmail()).build();

        List<String> testImageUrl = new ArrayList<>();
        testImageUrl.add("test url");
        testImageUrl.add("test url");

        GuideProduct product = GuideProduct.setGuideProduct(user, request, testImageUrl);
        request.getCategories().forEach(category -> {
            product.addGuideCategory(new GuideCategory(category));
        });

        return product;
    }
}
