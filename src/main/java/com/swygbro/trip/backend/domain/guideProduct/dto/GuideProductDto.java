package com.swygbro.trip.backend.domain.guideProduct.dto;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuideProductDto {
    private String title;
    private String description;
    private List<GuideImageDto> images;

    public static GuideProductDto fromEntity(GuideProduct product) {
        List<GuideImageDto> images = product.getImages().stream().map(GuideImageDto::fromEntity).toList();

        return new GuideProductDto(
                product.getTitle(),
                product.getDescription(),
                images
        );
    }
}
