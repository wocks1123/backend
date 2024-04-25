package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuideImageDto {
    private String url;

    public static GuideImageDto fromEntity(GuideImage image) {
        return new GuideImageDto(image.getUrl());
    }
}
