package com.swygbro.trip.backend.domain.reservation.domain;

import com.swygbro.trip.backend.domain.reservation.dto.ReservationSearchCriteria;

import java.util.List;

public interface ReservationCustomRepository {

    List<Reservation> findReservationsByClientId(Long clientId, ReservationSearchCriteria criteria);

    List<Reservation> findReservationsByGuideId(Long clientId, ReservationSearchCriteria criteria);
}
