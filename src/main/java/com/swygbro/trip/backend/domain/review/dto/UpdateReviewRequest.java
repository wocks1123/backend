package com.swygbro.trip.backend.domain.review.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateReviewRequest {
    private String content;
    private Integer rating;
    private List<String> images;
}
