package com.swygbro.trip.backend.domain.admin.dao;

import com.swygbro.trip.backend.domain.admin.dto.ReservationDetailDto;
import com.swygbro.trip.backend.global.status.PayStatus;
import com.swygbro.trip.backend.global.status.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationDao {
    Page<ReservationDetailDto> findReservationsByFilter(Pageable pageable,
                                                        Long id,
                                                        String merchantUid,
                                                        Long productId,
                                                        String client,
                                                        String guide,
                                                        PayStatus payStatus,
                                                        ReservationStatus reservationStatus);
}
