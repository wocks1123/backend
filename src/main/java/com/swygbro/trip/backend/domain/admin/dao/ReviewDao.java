package com.swygbro.trip.backend.domain.admin.dao;

import com.swygbro.trip.backend.domain.admin.dto.ReviewDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewDao {
    Page<ReviewDetailDto> findReviewsByFilter(Pageable pageable,
                                              Long id,
                                              String reviewer,
                                              Long guideProductId);
}
