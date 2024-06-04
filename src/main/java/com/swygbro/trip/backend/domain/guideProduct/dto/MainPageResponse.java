package com.swygbro.trip.backend.domain.guideProduct.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainPageResponse {
    @Schema(description = "추천 게시물들")
    private List<SearchGuideProductResponse> bestGuideProducts;
    @Schema(description = "근처 게시물들")
    private List<SearchGuideProductResponse> nearGuideProducts;
    @Schema(description = "전체 게시물들")
    private Page<SearchGuideProductResponse> allGuideProducts;

    public static MainPageResponse from(List<SearchGuideProductResponse> bestGuideProducts,
                                        List<SearchGuideProductResponse> nearGuideProducts,
                                        Page<SearchGuideProductResponse> allGuideProducts) {
        return MainPageResponse.builder()
                .nearGuideProducts(nearGuideProducts)
                .bestGuideProducts(bestGuideProducts)
                .allGuideProducts(allGuideProducts)
                .build();
    }
}
