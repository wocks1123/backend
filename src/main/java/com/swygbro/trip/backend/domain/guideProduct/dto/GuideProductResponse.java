package com.swygbro.trip.backend.domain.guideProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GuideProductResponse {
    private String title;
    private String description;
    private List<GuideImageDto> images;

    public static GuideProductResponse fromDto(GuideProductDto dto) {
        return new GuideProductResponse(dto.getTitle(), dto.getDescription(), dto.getImages());
    }
}
