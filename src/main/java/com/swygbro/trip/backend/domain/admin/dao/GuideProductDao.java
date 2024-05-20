package com.swygbro.trip.backend.domain.admin.dao;

import com.swygbro.trip.backend.domain.admin.dto.GuideProductDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuideProductDao {
    Page<GuideProductDetailDto> findGuideProductsByFilter(Pageable pageable,
                                                          Long id,
                                                          String title,
                                                          String nickname,
                                                          String locationName);
}
