package com.swygbro.trip.backend.domain.reservation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {
    public List<Reservation> findByGuideId(Long guideId);

    public Reservation findByMerchantUid(String merchantUid);
}
