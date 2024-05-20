package com.swygbro.trip.backend.domain.reservation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {
    List<Reservation> findByGuideId(Long guideId);

    Reservation findByMerchantUid(String merchantUid);

    Page<Reservation> findAllByGuideId(Long guideId, Pageable pageable);


    Page<Reservation> findAllByClient_Id(Long clientId, Pageable pageable);

}
