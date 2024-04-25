package com.swygbro.trip.backend.domain.guideProduct.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateGuideProductRequest {
    private String title;
    private String description;

}
